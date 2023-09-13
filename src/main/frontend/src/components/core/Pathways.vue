<template>
    <div class="path-container">
        <v-layout justify-center>
            <v-flex shrink>
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
            </v-flex>
        </v-layout>
        <v-dialog
            v-model="deleteDialog"
            max-width="290"
        >
            <v-card>
                <v-card-title class="headline">Delete path?</v-card-title>

                <v-card-text>
                    This will permanently remove this path and any associated videos from the database. This action
                    cannot be
                    undone.
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
                        @click="deletePath"
                        class="caption"
                    >
                        Delete
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog
            v-model="deleteCourseDialog"
            max-width="290"
        >
            <v-card>
                <v-card-title class="headline">Delete course?</v-card-title>

                <v-card-text>
                    This will permanently remove this course and any associated videos from the database. This action
                    cannot be
                    undone.
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn
                        color="primary"
                        text
                        @click="deleteCourseDialog = false"
                        class="caption"
                    >
                        Cancel
                    </v-btn>

                    <v-btn
                        color="primary"
                        text
                        @click="deleteCourse"
                        class="caption"
                    >
                        Delete
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <div>
            <v-progress-linear
                :active="importProgressVisible"
                v-model="importProgress"
                height="10"
                striped
                color="indigo darken-2"
            ></v-progress-linear>
        </div>
        <v-flex pb-2 d-flex>
            <v-flex d-inline>
                <span class="title font-weight-light">Pathways</span>
            </v-flex>
        </v-flex>
        <div class="mx-auto">
            <v-slide-group
                v-model="model"
                class="py3"
                center-active
                show-arrows
                mandatory
            >
                <!--region No Pathways-->
                <v-slide-item v-slot:default="{ active, toggle }" v-if="pathways.length === 0">
                    <v-sheet
                        :color="innerSheetColor"
                        class="ma-2"
                        height="225"
                        width="400"
                        tile
                    >
                        <v-layout column fill-height align-center justify-center>
                            <v-icon x-large>
                                mdi-alert-decagram-outline
                            </v-icon>
                            <v-subheader class="caption text-uppercase">No Paths Found</v-subheader>
                        </v-layout>
                    </v-sheet>
                </v-slide-item>
                <!--endregion-->
                <!--region Pathways-->
                <v-slide-item v-for="(path, i) in pathways" :key="i" v-slot:default="{ active, toggle }">
                    <a>
                        <v-sheet
                            :color="innerSheetColor"
                            class="ma-2"
                            height="225"
                            width="400"
                            tile
                            @click="toggle"
                            v-on:click="changeTab(model)"
                        >
                            <v-layout class="pb-5">
                                <v-flex pa-3 pb-5>
                                    <v-img style="height: 50px; width: 50px;" contain
                                           :src="path.pathImg || 'https://i.imgur.com/sQ0sFEf.png'"></v-img>
                                    <span class="subtitle-1 font-weight-medium apart">{{ path.title }}</span>
                                    <br>
                                    <small class="apart"><strong>{{ path.courses.length }}</strong>
                                        Course{{ path.courses.length !== 1 ? 's' : '' }}</small>
                                    <small>
                                        &emsp;
                                        <strong>{{ parseDuration(formatDuration(path.duration))[0] }}</strong>
                                        {{ parseDuration(formatDuration(path.duration))[1] }}
                                        <strong>{{ parseDuration(formatDuration(path.duration))[2] }}</strong>
                                        {{ parseDuration(formatDuration(path.duration))[3] }}
                                    </small>
                                </v-flex>
                            </v-layout>
                            <v-progress-linear height="2" color="white" class="mt-2"
                                               :value="getCompletion(path)"></v-progress-linear>
                            <v-toolbar flat :color="toolbarColor" height="60">
                                <v-toolbar-title class="caption"><strong>{{ getCompletion(path) }}%</strong> Complete
                                </v-toolbar-title>
                                <v-spacer></v-spacer>
                                <div v-if="isAdmin">
                                    <v-btn x-small icon @click="editPath(path)"
                                           style="height: 32px!important; width: 32px!important;">
                                        <v-icon small>mdi-pencil</v-icon>
                                    </v-btn>
                                    <v-btn x-small icon @click="promptDeletePath(path)"
                                           style="height: 32px!important; width: 32px!important;">
                                        <v-icon small>mdi-delete</v-icon>
                                    </v-btn>
                                </div>
                            </v-toolbar>
                        </v-sheet>
                    </a>
                </v-slide-item>
                <!--endregion-->
                <!--region Add Pathway-->
                <v-slide-item v-slot:default="{ active, toggle }">
                    <a v-if="isAdmin">
                        <v-sheet
                            :color="innerSheetColor"
                            class="ma-2"
                            height="225"
                            width="400"
                            tile
                            @click="toggle"
                            v-on:click="dialog = !dialog"
                        >
                            <v-layout column fill-height align-center justify-center>
                                <v-icon x-large>
                                    mdi-shape-rectangle-plus
                                </v-icon>
                                <v-subheader class="caption text-uppercase">Add Pathway</v-subheader>
                            </v-layout>
                            <v-layout row justify-center>
                                <v-dialog v-model="dialog" persistent max-width="600px">
                                    <v-card>
                                        <v-card-title>
                                            <span class="headline">{{ formTitle }}</span>
                                        </v-card-title>
                                        <v-card-text>
                                            <v-form ref="form" v-model="valid">
                                                <v-container grid-list-md>
                                                    <v-layout wrap>
                                                        <v-flex xs12>
                                                            <v-text-field label="Title" :rules="[rules.required]"
                                                                          v-model="editedItem.title"></v-text-field>
                                                        </v-flex>
                                                        <v-flex xs12>
                                                            <v-text-field label="Short Title" :rules="[rules.required]"
                                                                          v-model="editedItem.shortTitle"></v-text-field>
                                                        </v-flex>
                                                        <v-flex xs12>
                                                            <v-text-field label="Link to Logo"
                                                                          :rules="[rules.required, rules.url]"
                                                                          v-model="editedItem.pathImg"></v-text-field>
                                                        </v-flex>
                                                        <v-flex xs12>
                                                            <label class="font-weight-medium">Drag and drop to change
                                                                order of courses in path</label>
                                                            <draggable :list="editedItem.courses"
                                                                       class="list-group width-100"
                                                                       ghost-class="ghost"
                                                                       @start="dragging = true"
                                                                       @end="dragging = false">
                                                                <transition-group>
                                                                    <div v-for="(course, index) in editedItem.courses"
                                                                         :key="index">
                                                                        <v-card class="my-1 pa-2 " color="white">
                                                                            <span class="draggable-card">{{
                                                                                    course.title
                                                                                }}</span>
                                                                        </v-card>
                                                                    </div>
                                                                </transition-group>
                                                            </draggable>
                                                        </v-flex>
                                                    </v-layout>
                                                </v-container>
                                            </v-form>
                                        </v-card-text>
                                        <v-card-actions>
                                            <v-spacer></v-spacer>
                                            <v-btn color="primary" text @click="close" class="caption">Close</v-btn>
                                            <v-btn v-if="editedIndex === -1" color="primary" text @click="save"
                                                   class="caption">Save
                                            </v-btn>
                                            <v-btn v-else color="primary" text @click="update" class="caption">Update
                                            </v-btn>
                                        </v-card-actions>
                                    </v-card>
                                    <hr>
                                    <v-card v-if="editedIndex === -1">
                                        <v-card-title>
                                            <span class="headline">Import From Excel</span>
                                        </v-card-title>
                                        <v-card-text>
                                            <v-form ref="form" v-model="valid">
                                                <v-container grid-list-md>
                                                    <v-layout wrap>
                                                        <v-btn>
                                                            <input label="Import XLSX File"
                                                                   class='btn'
                                                                   type="file"
                                                                   multiple="false"
                                                                   id="sheetjs-input"
                                                                   accept=".xlsx"
                                                                   @change="excelFileInput"/>
                                                        </v-btn>
                                                    </v-layout>
                                                </v-container>
                                            </v-form>
                                        </v-card-text>
                                    </v-card>
                                </v-dialog>
                            </v-layout>
                        </v-sheet>
                    </a>
                </v-slide-item>
                <!--endregion-->
            </v-slide-group>
        </div>
        <v-flex pt-5 d-flex>
            <v-flex shrink>
                <span class="title font-weight-light pr-5">Courses</span>
            </v-flex>
            <v-flex shrink>
                <v-tabs
                    color="primary"
                    :icons-and-text="false"
                    v-model="tab"
                    centered
                    grow
                >
                    <v-tabs-slider></v-tabs-slider>
                    <v-tab v-for="i in tabs" :key="i" :href="`#tab-${i+1}`" :ripple="false">
                        {{ pathways[i - 1].shortTitle }}
                    </v-tab>
                </v-tabs>
            </v-flex>
            <v-spacer></v-spacer>
            <v-flex grow style="width: -webkit-fill-available"></v-flex>
        </v-flex>
        <v-tabs-items v-model="tab">
            <v-tab-item
                v-for="i in tabs"
                :key="i"
                :value="'tab-' + (i + 1)"
            >
                <v-sheet :color="sheetColor">
                    <v-layout wrap align-content-start justify-space-between>
                        <v-flex xs6 pa-2
                                v-for="(course, courseIndex) in pathways[i-1].courses"
                                :key="courseIndex"
                                v-if="course.title">
                            <a @click="processTarget($event, {path: i-1, course: courseIndex})">
                                <v-sheet tile :color="innerSheetColor" class="py-2" style="height: 100px;">
                                    <v-flex d-flex pa-4>
                                        <v-layout align-center>
                                            <v-flex xs1 mr-4>
                                                <v-img style="height: 50px; width: 50px;" contain
                                                       :src="course.courseImg || 'https://i.imgur.com/sQ0sFEf.png'"></v-img>
                                            </v-flex>
                                            <v-flex align-self-center>
                                                <span class="subtitle-1 font-weight-medium apart">{{
                                                        course.title
                                                    }}</span>
                                                <br>
                                                <small class="apart"><strong>{{ courseVideos(course) }}</strong>
                                                    Video{{ courseVideos(course) !== 1 ? "s" : "" }}</small>
                                                <small>&emsp;<strong>{{
                                                        parseDuration(formatDuration(pathways[i - 1].courses[courseIndex].duration))[0]
                                                    }}</strong> {{
                                                        parseDuration(formatDuration(pathways[i - 1].courses[courseIndex].duration))[1]
                                                    }} <strong>{{
                                                            parseDuration(formatDuration(pathways[i - 1].courses[courseIndex].duration))[2]
                                                        }}</strong>
                                                    {{
                                                        parseDuration(formatDuration(pathways[i - 1].courses[courseIndex].duration))[3]
                                                    }}</small>
                                            </v-flex>
                                            <v-spacer></v-spacer>
                                            <div v-if="isAdmin">
                                                <v-btn x-small icon>
                                                    <v-icon small>mdi-pencil</v-icon>
                                                </v-btn>
                                                <v-btn x-small icon>
                                                    <v-icon small>mdi-delete</v-icon>
                                                </v-btn>
                                            </div>
                                        </v-layout>

                                    </v-flex>
                                </v-sheet>
                            </a>
                        </v-flex>
                        <v-flex xs12 pa-2 v-else>
                            <v-sheet
                                :color="innerSheetColor"
                                tile
                                class="pa-4"
                                style="height: 100px;"
                                v-if="!isAdmin"
                            >
                                <v-layout column fill-height align-center justify-center>
                                    <v-icon x-large>
                                        mdi-alert-decagram-outline
                                    </v-icon>
                                    <v-subheader class="caption text-uppercase">No Courses Found</v-subheader>
                                </v-layout>
                            </v-sheet>
                        </v-flex>
                        <v-flex xs6 pa-2>
                            <a @click="routeTo('/pathways/' + (i - 1) + '/course/make', 'Course Builder')"
                               v-if="isAdmin">
                                <v-sheet
                                    :color="innerSheetColor"
                                    tile
                                    class="pa-4"
                                    style="height: 100px;"
                                >
                                    <v-layout column fill-height align-center justify-center>
                                        <v-icon x-large>
                                            mdi-file-document-box-plus-outline
                                        </v-icon>
                                        <v-subheader class="caption text-uppercase">Add Course</v-subheader>
                                    </v-layout>
                                </v-sheet>
                            </a>
                        </v-flex>
                    </v-layout>
                </v-sheet>
            </v-tab-item>
        </v-tabs-items>
    </div>
</template>

<script>
import common from '../../common.js';
import {mapGetters} from 'vuex';
import {AXIOS} from '@/http-commons';
import XLSX from 'xlsx';
import draggable from 'vuedraggable';

export default {
    name: "Pathways",
    components: {draggable},
    data() {
        return {
            dragging: false,
            alert: {
                active: false,
                message: '',
                color: undefined,
                btnColor: undefined,
                timeout: 0
            },
            deleteDialog: false,
            dialog: false,
            model: null,
            pathways: [],
            tab: null,
            text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
            tabs: 0,
            editedIndex: -1,
            editedCourse: {
                path: 0,
                course: 0
            },
            defaultCourse: {
                path: 0,
                course: 0
            },
            deleteCourseDialog: false,
            editedItem: {
                id: '',
                title: '',
                shortTitle: '',
                pathImg: '',
                courses: [],
                duration: {
                    hours: 0,
                    minutes: 0,
                    seconds: 0,
                    total: ''
                },
            },
            defaultItem: {
                id: '',
                title: '',
                shortTitle: '',
                pathImg: '',
                courses: [],
                duration: {
                    hours: 0,
                    minutes: 0,
                    seconds: 0
                },
            },
            rules: {
                required: value => !!value || 'Required.',
                url: value => {
                    const pattern = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/;
                    return pattern.test(value) || 'Invalid URL.'
                }
            },
            valid: false,
            importProgressVisible: false,
            importProgress: 0
        }
    },
    methods: {
        /**
         * Splits each part of the duration string into an array so each string can be visually formatted differently.
         * @param {string}
         * @returns {array}
         * @public
         */
        parseDuration(duration) {
            return duration.split(" ");
        },
        /**
         * Formats the total duration of a path and returns the populated information.
         * If an object only has minutes and seconds, it will return minutes and seconds.
         * If it has hours and minutes, it will return hours and minutes.
         * @param {Object} duration Object representing total duration of a path
         * @returns {string}
         * @public
         */
        formatDuration(duration) {
            if (duration) {
                if (duration.hours) {
                    if (duration.minutes) return duration.hours + " Hour" + (duration.hours !== 1 ? "s " : " ") + duration.minutes + " Minute" + (duration.minutes !== 1 ? "s" : "");
                    return duration.hours + " Hour" + (duration.hours !== 1 ? "s " : "");
                } else if (duration.minutes) {
                    if (duration.seconds) return duration.minutes + " Minute" + (duration.minutes !== 1 ? "s " : "") + duration.seconds + " Second" + (duration.seconds !== 1 ? "s" : "");
                    return duration.seconds + " Second" + (duration.seconds !== 1 ? "s" : "");
                } else if (duration.seconds) return duration.seconds + " Second" + +duration.seconds !== 1 ? "s " : "";
                else return duration.hours + " Hour" + (duration.hours !== 1 ? "s " : "") + duration.minutes + " Minute" + (duration.minutes !== 1 ? "s " : "");
            } else return "0"
        },
        /**
         * Iterates through the given path in the user's account and calculates the ratio of completed to unwatched videos.
         * @param {Object} path The path to find completion for
         * @returns {String}
         * @public
         */
        getCompletion(path) {
            let user = this.getUser;
            let completed = 0;
            let total = 0;
            if (user.paths) {
                for (let i = 0; i < user.paths.length; i++) {
                    if (user.paths[i].title === path.title) {
                        for (let j = 0; j < user.paths[i].courses.length; j++) {
                            for (let k = 0; k < user.paths[i].courses[j].content.length; k++) {
                                if (user.paths[i].courses[j].content[k].contentType === 'VIDEO') {
                                    total++;

                                    if (user.paths[i].courses[j].content[k].completed) {
                                        completed++;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return completed && total ? ((completed / total) * 100).toFixed(0) : "0";
        },
        /**
         * Some click events on an element overrode others, and affected intended behavior of the app.
         * This function aims to fix that. It processes what the user clicked on and performs the appropriate action.
         * @param {event} e Click event<br>
         * @param {Object} obj Target path
         * @returns {void}
         * @public
         */
        processTarget(e, obj) {
            if (e.target.classList.contains("v-btn")) {
                if (e.target.firstChild.firstChild.classList.contains("mdi-pencil")) {
                    this.push('/pathways/' + obj.path + '/course/' + obj.course + '/edit');
                } else if (e.target.firstChild.firstChild.classList.contains("mdi-delete")) {
                    this.promptDeleteCourse(obj);
                }
            } else if (e.target.classList.contains("v-btn__content")) {
                if (e.target.firstChild.classList.contains("mdi-pencil")) {
                    this.push('/pathways/' + obj.path + '/course/' + obj.course + '/edit');
                } else if (e.target.firstChild.classList.contains("mdi-delete")) {
                    this.promptDeleteCourse(obj);
                }
            } else if (e.target.classList.contains("mdi-pencil")) {
                this.push('/pathways/' + obj.path + '/course/' + obj.course + '/edit');
            } else if (e.target.classList.contains("mdi-delete")) {
                this.promptDeleteCourse(obj);
            } else {
                this.push('/player/pathway/' + this.hyphenCase(this.pathways[obj.path].title)
                    + '/course/' + this.hyphenCase(this.pathways[obj.path].courses[obj.course].title)
                    + '/video'
                )
            }
        },
        /**
         * Updates `this.tab` which will allow `v-tabs` to get the appropriate data for the tab
         * @param {Number} Index of the tab within the `v-tabs` element
         * @returns {void}
         * @public
         */
        changeTab(val) {
            // Val is 0-based but tab index is not.
            // First tab is tab-1, which is 'All'
            // You need to add 2 to tab in order to get the appropriate section.
            this.tab = 'tab-' + (val + 1);
        },
        /**
         * Opens an edit dialog and sets `this.editedItem` to the contents of path
         * @param {Object} path The path to be edited
         * @returns {void}
         * @public
         */
        editPath(path) {
            this.editedIndex = this.pathways.indexOf(path);
            this.editedItem = Object.assign({}, path);
            this.dialog = true;
        },
        /**
         * Opens a delete dialog and sets `this.editedItem` to the contents of path
         * @param {Object} path The path to be deleted
         * @returns {void}
         * @public
         */
        promptDeletePath(path) {
            this.deleteDialog = true;
            this.editedItem = path;
        },
        /**
         * Opens a delete dialog and sets `this.editedCourse` to the contents of course
         * @param {Object} course The course to be deleted
         * @returns {void}
         * @public
         */
        promptDeleteCourse(course) {
            this.deleteCourseDialog = true;
            this.editedCourse = course;
        },
        /**
         * Deletes a path with id equal to the id property of `this.editedItem`.
         * Will then also delete the path under each user account in the database.
         * If successful, a snackbar will appear that says "Path deleted successfully!"
         * @returns {void}
         * @public
         */
        deletePath() {
            let self = this;
            AXIOS.delete('/api/paths/' + this.editedItem.id)
                .then((response) => {
                    self.snackbar('success', 'Path deleted successfully!', 2.5);

                    self.tabs--;
                    self.model--;
                    self.pathways = self.pathways.filter((path) => path !== self.editedItem);

                    AXIOS.get('/api/users')
                        .then((response) => {
                            let users = response.data;

                            for (let i = 0; i < users.length; i++) {
                                if (users[i].paths) {
                                    users[i].paths = users[i].paths.filter((path) => path.title !== self.editedItem.title);

                                    AXIOS.post('/api/users', users[i])
                                        .then((response) => {
                                            if (response.data.username === self.getUser.username) {
                                                self.$store.dispatch({
                                                    type: 'setUser',
                                                    user: response.data
                                                })
                                            }
                                        })
                                        .catch((error) => {
                                            console.log(error);
                                            self.snackbar('error', 'An error occurred while updating users!', 2.5)
                                        })
                                }
                            }
                        })
                        .catch((error) => {
                            console.log(error);
                            self.snackbar('error', 'An error occurred while getting all users!', 2.5);
                        })

                    self.close();
                })
                .catch((error) => {
                    console.log(error);
                    self.close();
                    self.snackbar('error', 'An error occurred while deleting from the database!', 2.5);
                });
        },
        /**
         * Deletes a course from the path defined in `this.editedCourse`.
         * Will then also delete the course under each user account in the database.
         * @returns {void}
         * @public
         */
        deleteCourse() {
            let self = this;
            let pathIndex = this.editedCourse.path;
            let courseIndex = this.editedCourse.course;

            this.pathways[this.editedCourse.path].courses.splice(this.editedCourse.course, 1);
            this.close();

            this.editedItem = Object.assign({}, this.pathways[this.editedCourse.path]);

            AXIOS.put('/api/paths', this.editedItem)
                .then((response) => {
                    self.snackbar('success', 'Course deleted successfully!', 2.5);

                    AXIOS.get('/api/users')
                        .then((response) => {
                            let users = response.data;

                            for (let i = 0; i < users.length; i++) {
                                users[i].paths[pathIndex].courses.splice(courseIndex, 1);

                                AXIOS.post('/api/users', users[i])
                                    .then((response) => {
                                        if (response.data.username === self.getUser.username) {
                                            self.$store.dispatch({
                                                type: 'setUser',
                                                user: response.data
                                            })
                                        }
                                    })
                                    .catch((error) => {
                                        console.log(error);
                                        self.snackbar('error', 'An error occurred while updating users!', 2.5)
                                    })
                            }
                        })
                        .catch((error) => {
                            console.log(error);
                            self.snackbar('error', 'An error occurred while getting users from the database!', 2.5)
                        })
                })
                .catch((error) => {
                    console.log(error);
                    self.snackbar('error', 'An error occurred while saving to the database!', 2.5)
                })
        },
        /**
         * Closes all dialogs and returns `this.editedItem` to it's defaults
         * @returns {void}
         * @public
         */
        close() {
            this.dialog = false;
            this.deleteDialog = false;
            this.deleteCourseDialog = false;
            setTimeout(() => {
                this.editedItem = Object.assign({}, this.defaultItem);
                this.editedCourse = Object.assign({}, this.defaultCourse);
                if (this.$refs.form) {
                    this.$refs.form.reset();
                }
                this.editedIndex = -1
            }, 300)
        },
        /**
         * Generates an RFC4122 compliant UUID, credit to https://stackoverflow.com/a/21963136
         * @returns {String}
         * @public
         */
        generateUUID() {
            let lut = [];
            for (let i = 0; i < 256; i++) {
                lut[i] = (i < 16 ? '0' : '') + (i).toString(16);
            }

            function e7() {
                let d0 = Math.random() * 0xffffffff | 0;
                let d1 = Math.random() * 0xffffffff | 0;
                let d2 = Math.random() * 0xffffffff | 0;
                let d3 = Math.random() * 0xffffffff | 0;
                return lut[d0 & 0xff] + lut[d0 >> 8 & 0xff] + lut[d0 >> 16 & 0xff] + lut[d0 >> 24 & 0xff] + '-' +
                    lut[d1 & 0xff] + lut[d1 >> 8 & 0xff] + '-' + lut[d1 >> 16 & 0x0f | 0x40] + lut[d1 >> 24 & 0xff] + '-' +
                    lut[d2 & 0x3f | 0x80] + lut[d2 >> 8 & 0xff] + '-' + lut[d2 >> 16 & 0xff] + lut[d2 >> 24 & 0xff] +
                    lut[d3 & 0xff] + lut[d3 >> 8 & 0xff] + lut[d3 >> 16 & 0xff] + lut[d3 >> 24 & 0xff];
            }

            return e7();
        },
        excelFileInput(evt) {
            let self = this;

            let file;
            let files = evt.target.files;

            if (!files || files.length == 0) return;

            file = files[0];

            const reader = new FileReader();
            reader.onload = async function (e) {
                // pre-process data
                let binary = "";
                let bytes = new Uint8Array(e.target.result);
                let length = bytes.byteLength;
                for (let i = 0; i < length; i++) {
                    binary += String.fromCharCode(bytes[i]);
                }

                /* read workbook */
                let workbook = XLSX.read(binary, {type: 'binary'});

                /* grab first sheet */
                let first_worksheet = workbook.Sheets[workbook.SheetNames[0]];
                let data = XLSX.utils.sheet_to_json(first_worksheet, {header: 1});

                let tmpPath = {
                    courses: []
                };

                // col 0 = TYPE
                // col 1 = TITLE
                // col 2 = DESC
                // col 3 = URL

                let totalPathDuration = 0;
                self.importProgressVisible = true;

                for (let i = 0; i < data.length; i++) {
                    self.importProgress = (i / data.length) * 100;
                    let row = data[i];

                    if ("PATH" === row[0]) {
                        tmpPath.title = row[1];
                        tmpPath.shortTitle = row[2];
                        tmpPath.pathImg = row[3];
                    }

                    if ("COURSE" === row[0]) {
                        // create and initialize course array
                        let courseVideos = [];
                        let totalCourseDuration = 0;
                        let _title = row[1];
                        let _desc = row[2];
                        let _url = row[3];

                        // move to the next row
                        if (i + 1 < data.length) {
                            i++;
                            row = data[i];
                        }

                        // loop until next COURSE is found
                        while (!("COURSE" === row[0])) {
                            if ("VIDEO" === row[0]) {

                                await self.getDuration(row[3]).then((duration) => {
                                    totalCourseDuration += duration;
                                    courseVideos.push({
                                        number: courseVideos.length + 1,
                                        title: row[1],
                                        description: row[2],
                                        url: row[3],
                                        duration: duration,
                                        panel: [0, 1],
                                        contentType: 'VIDEO',
                                        completed: false
                                    });
                                });

                            } else if ("LAB" === row[0]) {
                                courseVideos.push({
                                    number: courseVideos.length + 1,
                                    title: row[1],
                                    description: row[2],
                                    url: row[3],
                                    panel: [0, 1],
                                    contentType: 'LAB',
                                    completed: false
                                });
                            } else {
                                console.log("TYPE error.  unknown TYPE, must be VIDEO or LAB");
                            }

                            // move to the next row
                            if (i + 1 < data.length) {
                                i++;
                                row = data[i];
                                self.importProgress = (i / data.length) * 100;
                            } else {
                                break;
                            }
                        }

                        // pull it back one
                        // cause the for loop will increment it for us
                        i--;

                        // add the populated course to the path
                        tmpPath.courses.push({
                            content: courseVideos,
                            tags: [],
                            title: _title,
                            desc: _desc,
                            courseImg: _url,
                            duration: {
                                hours: Math.floor(totalCourseDuration / 3600),
                                minutes: Math.floor(totalCourseDuration % 3600 / 60),
                                seconds: Math.floor(totalCourseDuration % 3600 % 60)
                            },
                        });
                        totalPathDuration += totalCourseDuration;

                    }
                }

                // path structure built completely
                tmpPath.duration = {
                    hours: Math.floor(totalPathDuration / 3600),
                    minutes: Math.floor(totalPathDuration % 3600 / 60),
                    seconds: Math.floor(totalPathDuration % 3600 % 60)
                }

                // ready for save
                self.editedItem = tmpPath;
                self.valid = true;  // hack to get around validation check
                self.save();

                self.importProgressVisible = false;
            };

            self.close();
            reader.readAsArrayBuffer(file);
        },
        getDuration(url) {
            let videoNode = document.createElement("video");
            let promise = new Promise(function (resolve, reject) {
                videoNode.addEventListener("loadedmetadata", function () {
                    resolve(videoNode.duration);
                });
                videoNode.addEventListener("error", function () {
                    reject(videoNode.error.message + "(" + videoNode.error.code + ")");
                });
            });

            videoNode.src = url;

            return promise;
        },
        /**
         * Saves a path with contents equal to `this.editedItem`.
         * Will then also save the path under each user account in the database.
         * If successful, a snackbar will appear that says "Path added successfully!"
         * If unsuccessful, it will say "An error occurred while adding to the database!"
         * @returns {void}
         * @public
         */
        save() {
            if (this.valid) {
                let self = this;
                // TODO why was this being generated instead of being saved
                // this.editedItem.id = this.generateUUID();
                AXIOS.post('/api/paths', this.editedItem)
                    .then((response) => {
                        self.snackbar('success', "Path added successfully!", 2.5);

                        if (self.editedIndex > -1) {
                            Object.assign(self.pathways[self.editedIndex], self.editedItem)
                        } else {
                            self.pathways.push(self.editedItem)
                        }

                        this.close();
                        this.model++;
                        this.tabs++;

                        AXIOS.get('/api/users')
                            .then((response) => {
                                if (response.status !== 204) {
                                    let users = response.data;

                                    for (let i = 0; i < users.length; i++) {
                                        if (users[i].paths) {
                                            users[i].paths.push(self.editedItem);

                                            AXIOS.post('/api/users', users[i])
                                                .then((response) => {
                                                    console.log(response)
                                                    if (response.data.username === self.getUser.username) {
                                                        console.log(true)
                                                        self.$store.dispatch({
                                                            type: 'setUser',
                                                            user: response.data
                                                        })
                                                    }
                                                })
                                                .catch((error) => {
                                                    console.log(error);
                                                    self.snackbar('error', 'An error occurred while updating users!', 2.5)
                                                })
                                        }
                                    }
                                }

                                self.close();

                            })
                            .catch((error) => {
                                console.log(error);
                                self.snackbar('error', 'An error occurred while getting all users!', 2.5);
                            })
                    })
                    .catch((error) => {
                        self.snackbar('error', 'An error occurred while saving to the database!', 2.5);
                        console.log(error);
                        this.close();
                    });
            } else {
                this.snackbar('warning', 'Please fill out all required fields!', 2.5);
            }
        },
        /**
         * Updates a path with id equal to the id property of `this.editedItem`.
         * Will then also update the path under each user account in the database.
         * If successful, a snackbar will appear that says "Path updated successfully!"
         * @returns {void}
         * @public
         */
        update() {
            if (this.valid) {
                let self = this;

                AXIOS.put('/api/paths', this.editedItem)
                    .then((response) => {
                        self.snackbar('success', "Path updated successfully!", 2.5);

                        let pathIndex = -1;

                        for (let i = 0; i < this.pathways.length; i++) {
                            if (this.pathways[i].id === this.editedItem.id) {
                                pathIndex = i;
                            }
                        }
                        this.pathways[pathIndex] = this.editedItem;


                        AXIOS.get('/api/users')
                            .then((response) => {
                                let users = response.data;

                                for (let i = 0; i < users.length; i++) {
                                    if (users[i].paths) {
                                        users[i].paths[pathIndex].title = self.editedItem.title;
                                        users[i].paths[pathIndex].shortTitle = self.editedItem.shortTitle;
                                        users[i].paths[pathIndex].pathImg = self.editedItem.pathImg;

                                        const updatedCourses = this.editedItem.courses;
                                        let userPathCourses = [...users[i].paths[pathIndex].courses];

                                        for (let i = 0; i < userPathCourses.length; i++) {
                                            // if the course doesn't match the course int the updated path
                                            if (userPathCourses[i].title !== updatedCourses[i].title) {
                                                // find where the course in the updated path currently exists in the user path
                                                for (let x = i + 1; x < userPathCourses.length; x++) {
                                                    if (userPathCourses[x].title === updatedCourses[i].title) {
                                                        userPathCourses.splice(i, 0, userPathCourses[x]);
                                                        userPathCourses.splice(x + 1, 1);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        // set updated course order to users path
                                        users[i].paths[pathIndex].courses = userPathCourses;
                                        AXIOS.post('/api/users', users[i])
                                            .then((response) => {
                                                if (response.data.username === self.getUser.username) {
                                                    console.log(response.data)
                                                    self.$store.dispatch({
                                                        type: 'setUser',
                                                        user: response.data
                                                    })
                                                }
                                            })
                                            .catch((error) => {
                                                console.log(error);
                                                self.snackbar('error', 'An error occurred while updating users!', 2.5)
                                            })
                                    }
                                }

                                self.close();
                            })
                            .catch((error) => {
                                console.log(error);
                                self.snackbar('error', 'An error occurred while getting users from the database!', 2.5);
                            })
                    })
                    .catch((error) => {
                        self.snackbar('error', 'An error occurred while saving to the database!', 2.5);
                        console.log(error);
                        this.close();
                    });
            } else {
                this.snackbar('warning', 'Please fill out all required fields!', 2.5);
            }
        },
        /**
         * Gets all paths and appends each to `this.pathways`.
         * Updates the number of tabs to equal the number of paths.
         * @returns {void}
         * @public
         */
        initPage() {
            let self = this;
            AXIOS.get('/api/paths')
                .then((response) => {
                    if (response.status === 204) {
                        self.snackbar('info', 'There were no paths found in the database!', 5);
                    } else {
                        response.data.forEach(path => {
                            self.pathways.push(path);
                            self.tabs++;
                        })
                    }
                })
                .catch((error) => {
                    console.log(error);
                    if (!error.status) {
                        self.snackbar('error', 'A network error occurred!', 5);
                        if (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') {
                            console.warn("A network error has occurred! This likely occurred due to one or more of the following reasons:")
                            if (window.location.hostname + ':' + window.location.port === "localhost:8081") {
                                console.warn("Authentication method in application-prod.yaml is set to LDAP");
                            }
                            console.warn("Server port in application-prod.yaml may be set to port 443");
                            console.warn("Base or proxy URL in http-commons.js may have been modified and is no longer correct");
                        }
                    }
                });

            this.tabs = this.pathways.length;
        },
        /**
         * Initializes the user's account. This will only run one time per user, ever.
         * If the user has not accessed any paths yet, this method will get all paths
         * from the database and put them into the user's account. It will also initialize
         * every video to `completed: false`.
         * @returns {void}
         * @public
         */
        initUser() {
            let self = this;
            let user = this.getUser;
            if (user.paths === null || user.paths && user.paths.length === 0) {
                AXIOS.get('/api/paths')
                    .then((response) => {
                        if (response.status === 204) {
                            self.snackbar('info', 'There were no paths found in the database!', 5)
                        } else {
                            let paths = response.data;

                            for (let i = 0; i < paths.length; i++) {
                                for (let j = 0; j < paths[i].courses.length; j++) {
                                    paths[i].courses[j].lastVideo = null;
                                    for (let k = 0; k < paths[i].courses[j].content.length; k++) {
                                        paths[i].courses[j].content[k].completed = false;
                                    }
                                }
                            }

                            user.paths = paths;

                            self.$store.dispatch({
                                type: 'setUser',
                                user: user
                            }).catch((error) => {
                                console.log(error);
                                self.snackbar('error', 'An error occurred while initializing your account!', 2.5)
                            });
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                        if (!error.status) {
                            self.snackbar('error', 'A network error occurred!', 5)
                        }

                    })
            }
        },
        /**
         * Returns the total number of videos in course
         * @returns {Number}
         * @public
         */
        courseVideos(course) {
            if (course && course.content) {
                return course.content.filter((content) => content.contentType === 'VIDEO').length;
            } else return 0;
        }
    },
    mounted() {
        let self = this;
        this.push = common.push;
        this.routeTo = common.routeTo;
        this.log = common.log;
        this.snackbar = common.snackbar;
        this.hyphenCase = common.hyphenCase;

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

        this.initPage();
        this.initUser();
    },
    computed: {
        isAdmin() {
            return this.getRole === 'ADMIN'
        },
        darkMode() {
            return this.$vuetify.theme.dark;
        },
        sheetColor() {
            return this.darkMode ? '#303030' : '#FAFAFA';
        },
        innerSheetColor() {
            return this.darkMode ? 'grey darken-4' : 'rgb(215, 233, 255)';
        },
        toolbarColor() {
            return this.darkMode ? 'grey darken-3' : 'rgb(183, 228, 255)';
        },
        formTitle() {
            return this.editedIndex === -1 ? 'New Pathway' : 'Edit Pathway'
        },
        allCourses() {
            let allCourses = [];
            for (let i = 0; i < this.pathways.length; i++) {
                for (let j = 0; j < this.pathways[i].courses.length; j++) {
                    if (this.pathways[i].courses[j].title) {
                        allCourses.push(this.pathways[i].courses[j]);
                    }
                }
            }

            return allCourses;
        },
        ...mapGetters([
            'getRole',
            'getUser',
            'getUserPaths'
        ])
    },
    watch: {
        /**
         * Runs whenever the `dialog` variable changes.
         * Will close the dialog if `dialog` equals `false`
         * @param {boolean} val Denotes whether or not dialog should be open <br>
         * @returns {void}
         * @public
         */
        dialog(val) {
            val || this.close()
        },
    }
}
</script>

<style scoped>
.apart::before {
    content: '';
    display: block;
}

.path-container {
    margin: 20px;
}

.draggable-card {
    color: black;
}
</style>

<docs>
Allows for full-CRUD functionality for paths and partial-CRUD functionality for courses. Also connects paths and courses
together in
a meaningful way. Displays user's overall progress for a path as well as the total duration of each course.

---
</docs>