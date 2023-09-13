<template>
    <v-app>
        <v-snackbar
                v-model="alert.active"
                top
                :color="alert.color"
                :timeout="0"
        >
            {{ alert.message }}
            <v-btn
                    :color="alert.btnColor"
                    text
                    @click="alert.active = !alert.active"
                    class="caption"
            >
                Close
            </v-btn>
        </v-snackbar>
        <v-dialog v-model="dialog" persistent max-width="600px">
            <v-card>
                <v-card-title>
                    <span class="headline">Edit Note</span>
                </v-card-title>
                <v-card-text class="pb-0">
                    <v-form ref="form" v-model="valid">
                        <v-container grid-list-md>
                            <v-layout wrap>
                                <v-flex xs12>
                                    <v-textarea label="Note" :rules="[rules.required]" v-model="editedItem.note" solo filled></v-textarea>
                                </v-flex>
                            </v-layout>
                        </v-container>
                    </v-form>
                </v-card-text>
                <v-card-actions class="pt-0">
                    <v-spacer></v-spacer>
                    <v-btn color="primary" text @click="dialog = false" class="caption">Close</v-btn>
                    <v-btn color="primary" text @click="update" class="caption">Update</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog
                v-model="deleteDialog"
                max-width="290"
        >
            <v-card>
                <v-card-title class="headline">Delete note?</v-card-title>

                <v-card-text>
                    This note will be permanently removed from your account. This action cannot be undone.
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn
                            color="primary"
                            text
                            @click="deleteDialog = false"
                            class="caption"
                    >
                        Cancel
                    </v-btn>

                    <v-btn
                            color="primary"
                            text
                            @click="deleteNote"
                            class="caption"
                    >
                        Delete
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-container ma-0>
            <v-flex pb-2 d-flex>
                <v-flex d-inline>
                    <span class="title font-weight-light">Notes</span>
                    <v-subheader class="caption pa-0">{{ getNotes.length }} note{{ getNotes.length > 1 || getNotes.length === 0 ? 's' : ''  }} on {{ coursesWithNotes }} course{{ coursesWithNotes > 1 || coursesWithNotes === 0 ? 's' : '' }}</v-subheader>
                </v-flex>
            </v-flex>
            <v-expansion-panels focusable multiple>
                <v-expansion-panel
                        v-for="(course, courseIndex) in courses"
                        :key="courseIndex"
                >
                    <v-expansion-panel-header>
                        <v-flex>
                            <v-card-title class="title py-0">{{ getCourseTitle(course.course) }}</v-card-title>
                            <v-subheader class="caption pt-0">{{ course.notes.length }} note{{ course.notes.length > 1 ? 's' : ''}} &emsp; &emsp; Last note added {{ timeSince(course)}} ago</v-subheader>
                        </v-flex>
                    </v-expansion-panel-header>
                    <v-expansion-panel-content :class="{ 'pt-4' : noteIndex === 0 }"
                                               v-for="(note, noteIndex) in course.notes"
                                               :key="note.note"
                    >
                        {{ note.note }}
                        <v-layout fill-height align-center class="float-right">
                            <v-flex >
                                <v-btn x-small icon @click="editNote(note)">
                                    <v-icon small>mdi-pencil</v-icon>
                                </v-btn>
                                <v-btn x-small icon @click="promptDelete(note)">
                                    <v-icon small>mdi-delete</v-icon>
                                </v-btn>
                            </v-flex>
                        </v-layout>
                        <v-flex>
                            <a @click="toVideo(note.video, note.timestamp)">
                                <v-icon small color="primary">mdi-play-circle-outline</v-icon>
                                <small class="pl-1">{{ getVideoTitle(note.video) }} {{ note.timestamp ? formatTimestamp(note.timestamp) : '' }}</small>
                            </a>
                        </v-flex>
                        <v-divider class="mt-3" v-if="noteIndex !== course.notes.length - 1"></v-divider>
                    </v-expansion-panel-content>
                </v-expansion-panel>
                <v-flex>
                    <v-sheet v-if="courses.length === 0" class="py-3">
                        <v-layout column fill-height align-center justify-center>
                            <v-icon x-large>
                                mdi-help-rhombus-outline
                            </v-icon>
                            <v-subheader class="caption text-uppercase">No notes found</v-subheader>
                        </v-layout>
                    </v-sheet>
                </v-flex>
            </v-expansion-panels>
        </v-container>
    </v-app>
</template>

<script>
    import common from '../../common'
    import {mapGetters} from 'vuex'
    import {AXIOS} from '../../http-commons'

    export default {
        name: "Notes",
        data() {
            return {
                alert: {
                    active: false,
                    message: '',
                    color: undefined,
                    btnColor: undefined,
                    timeout: 0
                },
                courses: [],
                defaultItem: {
                    note: '',
                    course: {},
                    video: {},
                    path: {},
                    timestamp: 0,
                    date: null
                },
                dialog: false,
                deleteDialog: false,
                editedIndex: -1,
                editedItem: {
                    note: '',
                    course: {},
                    video: {},
                    path: {},
                    timestamp: 0,
                    date: null
                },
                rules: {
                    required: value => !!value || 'Required.',
                },
                valid: false
            }
        },
        methods: {
            /**
             * Iterates through the user's videos and sets the last video watched to the video that was clicked on.
             * This is necessary due to how the VideoProcessor component works.
             * Once it has set the last video, it will reroute the user to the VideoProcessor.
             * @param {string} video Hyphen-cased title of video<br>
             * @param {Number} timestamp Timestamp of note in raw seconds
             * @returns {string}
             * @public
             */
            toVideo(video, timestamp) {
                let self = this;
                let user = this.getUser;

                for (let i = 0; i < user.paths.length; i++) {
                    for (let j = 0; j < user.paths[i].courses.length; j++) {
                        for (let k = 0; k < user.paths[i].courses[j].content.length; k++) {
                            if (this.hyphenCase(user.paths[i].courses[j].content[k].title) === video) {
                                this.$store.dispatch({
                                    type: 'setLastVideo',
                                    lastVideo: user.paths[i].courses[j].content[k],
                                    videoIndex: k,
                                    courseIndex: j,
                                    path: user.paths[i],
                                }).then(() => {
                                    self.push('/pathways/' + self.hyphenCase(user.paths[i].title) + '/course/' + self.hyphenCase(user.paths[i].courses[j].title) + '/videos?timestamp=' + timestamp.toFixed(0))
                                }).catch((error) => {
                                    console.log(error);
                                })
                            }
                        }
                    }
                }
            },
            /**
             * Takes raw seconds and converts it to a timestamp format (ex. 64 seconds --> 1:04, 125 seconds --> 2:05)
             * @param {Number} totalSeconds Amount of time solely represented in seconds<br>
             * @returns {string}
             * @public
             */
            formatTimestamp(totalSeconds) {
                let minutes = Math.floor(totalSeconds / 60);
                let seconds = Math.round(totalSeconds - minutes * 60);

                if (Number(seconds) < 10) {
                    seconds = '0' + seconds;
                }

                return minutes + ":" + seconds
            },
            /**
             * Iterates through all courses in a user's account and finds one with a title that matches the hyphen-cased title (ex. Object Oriented Programming --> object-oriented-programming)
             * @param {string} course Hyphen-cased title of course<br>
             * @returns {string}
             * @public
             */
            getCourseTitle(course) {
                let user = this.getUser;

                for (let i = 0; i < user.paths.length; i++) {
                    for (let j = 0; j < user.paths[i].courses.length; j++) {
                        if (this.hyphenCase(user.paths[i].courses[j].title) === course) {
                            return user.paths[i].courses[j].title
                        }
                    }
                }
            },
            /**
             * Iterates through all videos in a user's account and finds one with a title that matches the hyphen-cased title (ex. Object Oriented Programming --> object-oriented-programming)
             * @param {string} video Hyphen-cased title of video<br>
             * @returns {string}
             * @public
             */
            getVideoTitle(video) {
                let user = this.getUser;
                const INVALID_TITLE = 'INVALID VIDEO TITLE'

                for (let i = 0; i < user.paths.length; i++) {
                    for (let j = 0; j < user.paths[i].courses.length; j++) {
                        for (let k = 0; k < user.paths[i].courses[j].content.length; k++) {
                            if (this.hyphenCase(user.paths[i].courses[j].content[k].title) === video) {
                                return user.paths[i].courses[j].content[k].title || INVALID_TITLE
                            }
                        }
                    }
                }
                return INVALID_TITLE;
            },
            /**
             * Calculates the amount of time that has passed since a note was last created and returns something similar to: "2 hours" or "6 weeks"
             * @param {Object} course Course from user's account <br>
             * @returns {string}
             * @public
             */
            timeSince(course) {
                let timestamps = [];

                for (let i = 0; i < course.notes.length; i++) {
                    timestamps.push(course.notes[i].date);
                }

                let today = new Date(Date.now());
                let newestNote = new Date(Math.max(...timestamps)); // Create date from largest (therefore newest) timestamp in the array

                let utcToday = Date.UTC(today.getFullYear(), today.getMonth(), today.getDate(), today.getHours(), today.getMinutes(), today.getSeconds());
                let utcBefore = Date.UTC(newestNote.getFullYear(), newestNote.getMonth(), newestNote.getDate(), newestNote.getHours(), newestNote.getMinutes(), newestNote.getSeconds());
                let timeDifference = utcToday - utcBefore;

                if (!utcBefore) {
                    return 'an unknown amount of time';
                }

                else if (timeDifference <= 60000) return this.pluralizeUnits(Math.floor(timeDifference * 0.001), 'second');
                else if (timeDifference > 60000 && timeDifference <= 3600000) return this.pluralizeUnits(Math.floor(timeDifference * 0.0000166667), 'minute');
                else if (timeDifference > 3600000 && timeDifference <= 86400000) return this.pluralizeUnits(Math.floor(timeDifference * 0.00000027778), 'hour');
                else if (timeDifference > 86400000 && timeDifference <= 604800000) return this.pluralizeUnits(Math.floor(timeDifference * 0.000000011574), 'day');
                else if (timeDifference > 604800000 && timeDifference <= 2628000000) return this.pluralizeUnits(Math.floor(timeDifference * 0.0000000016534), 'week');
                else if (timeDifference > 2628000000 && timeDifference <= 31536000000) return this.pluralizeUnits(Math.floor(timeDifference * 0.00000000038052), 'month');
                else if (timeDifference > 31536000000) return this.pluralizeUnits(Math.floor(timeDifference * 0.00000000003171), 'year', 'more than');
                else return timeDifference.toString();
            },
            /**
             * Normalizes input to a similar format as: "More than 2 years" or "Less than 1 minute"
             * @param {Number} time Amount of time <br>
             * @param {String} unit Unit of measure (ex. minute, hour, etc.) <br>
             * @param {String} prefix (Optional) Anything you want to prefix to the output <br>
             * @returns {string}
             * @public
             */
            pluralizeUnits(time, unit, prefix='') {
                if (time === 1) return prefix + ' ' + time + ' ' + unit
                else if (time > 1) return prefix + ' ' + time + ' ' + unit + 's'
            },
            /**
             * Opens a dialog and populates it's text fields with the contents of this.editedItem.
             * @param {Object} note
             * @returns {void}
             * @public
             */
            editNote(note) {
                this.editedIndex = this.courses.indexOf(note);
                this.editedItem = Object.assign({}, note);
                this.dialog = true;
            },
            /**
             * Opens a dialog asking the user to confirm that they want to delete the specified note.
             * @param {Object} note
             * @returns {void}
             * @public
             */
            promptDelete(note) {
                this.deleteDialog = true;
                this.editedItem = note;
            },
            /**
             * Locates the note in VueX that matches the contents of `this.editedItem` and filters it out
             * If successful, a snackbar will appear and say "Note removed!"
             * If unsuccessful, it will say "An error occurred while removing the note!"
             * @returns {void}
             * @public
             */
            deleteNote() {
                let self = this;

                this.$store.dispatch({
                    type: 'deleteNote',
                    note: this.editedItem
                }).then(() => {
                    for (let i = 0; i < self.courses.length; i++) {
                        if (self.courses[i].course === self.editedItem.course) {
                            self.courses[i].notes = self.courses[i].notes.filter((note) => note !== self.editedItem)
                            self.snackbar('success', 'Note removed!', 2.5);
                        }
                    }
                }).catch((error) => {
                    console.log(error)
                    self.snackbar('error', 'An error occurred while removing the note!', 2.5);
                });

                this.close();
                this.courses = this.allCourses();
            },
            /**
             * Closes any open dialogs and reverts this.editedItem to its default values.
             * @returns {void}
             * @public
             */
            close() {
                this.dialog = false;
                this.deleteDialog = false;
                setTimeout(() => {
                    this.editedItem = Object.assign({}, this.defaultItem)
                    this.editedIndex = -1
                }, 300)
            },
            /**
             * Takes the contents of this.editedItem and stores it in VueX.
             * If successful, a snackbar will appear and say "Note successfully updated!"
             * If unsuccessful, it will say "An error occurred while updating the note!"
             * @returns {void}
             * @public
             */
            update() {
                if (this.valid) {
                    let self = this;
                    for (let i = 0; i < this.getNotes.length; i++) {
                        // date stored in milliseconds, is therefore a unique identifier
                        if (this.getNotes[i].date === this.editedItem.date) {
                            this.$store.dispatch({
                                type: 'updateNote',
                                noteIndex: i,
                                note: this.editedItem
                            }).then(() => {
                                for (let j = 0; j < self.courses.length; j++) {
                                    if (self.courses[i].course === self.editedItem.course) {
                                        self.courses[i].notes[i] = this.editedItem;
                                        self.snackbar('success', 'Note successfully updated!', 2.5);
                                    }
                                }
                            }).catch((error) => {
                                self.snackbar('error', 'An error occurred while updating the note!', 2.5);
                                console.log(error);
                            })
                        }
                    }
                    this.close();
                }
                else {
                    this.snackbar('warning', 'Please fill out all required fields!', 2.5);
                }
            },
            /**
             * Returns all courses that have notes under the current user
             * @returns {array}
             * @public
             */
            allCourses() {
                let notes = this.getNotes;
                let allCourses = [];

                // Iterate through all notes and collect the course and content of the note
                for (let i = 0; i < notes.length; i++) {
                    allCourses.push({
                        course: notes[i].course,
                        notes: notes.filter((note) => note.course === notes[i].course)
                    })
                }

                // Remove duplicates from allCourses
                for (let i = 0; i < allCourses.length; i++) {
                    for (let j = 0; j < allCourses.length; j++) {
                        if (i !== j) {
                            if (JSON.stringify(allCourses[i]) === JSON.stringify(allCourses[j])) {
                                delete allCourses[j];
                            }
                        }
                    }
                }

                // Remove empty values
                allCourses = allCourses.filter((course) => !!course);

                return allCourses;
            }
        },
        created() {
            this.courses = this.allCourses();
            this.push = common.push;
            this.hyphenCase = common.hyphenCase;
            this.snackbar = common.snackbar;

            this.$on('openAlert', (msg) => {
                this.alert = {
                    active: true,
                    color: msg.color,
                    message: msg.alertMessage,
                    timeout: 0
                };
                setTimeout(() => {
                    this.alert.active = false;
                }, msg.duration * 1000);
            });
        },
        computed: {
            /**
             * Takes the specified methods from vuex and generates an equivalent method declaration as follows:
             *
             * this.$store.dispatch({
             *     type: FUNCTION_NAME
             * }
             *
             */
            ...mapGetters([
                'getNotes',
                'getUser'
            ]),
            /**
             * Returns true or false if dark mode is enabled or disabled respectively
             * @returns {boolean}
             * @public
             */
            darkMode() {
                return this.$vuetify.theme.dark;
            },
            coursesWithNotes() {
                let notes = this.getNotes;
                let courses = [];

                for (let i = 0; i < notes.length; i++) {
                    if (!courses.includes(notes[i].course)) {
                        courses.push(notes[i].course);
                    }
                }

                return courses.length;
            },
        },
        beforeDestroy() {
            let user = this.getUser;
            let self = this;
            AXIOS.post('/api/users', user)
                .then((response) => {
                    self.$store.dispatch({
                        type: 'setUser',
                        user: response.data
                    })
                })
                .catch((error) => {
                    console.log(error);
                })
        }
    }
</script>

<style scoped>
    .v-subheader {
        height: 24px;
    }
</style>

<docs>
    Allows partial-CRUD functionality for notes. Users may edit, delete, or view notes here.

    ---
</docs>