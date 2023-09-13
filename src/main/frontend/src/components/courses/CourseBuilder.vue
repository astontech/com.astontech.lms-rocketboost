<template>
    <v-app>
        <v-container ma-0>
            <v-flex pb-2 d-flex>
                <v-flex d-inline>
                    <span class="title font-weight-light">Course Builder</span>
                    <v-subheader class="caption pa-0">{{ videoCount() }} video{{ videoCount !== 1 ? 's' : '' }} on 1 course</v-subheader>
                </v-flex>
            </v-flex>
            <v-layout row justify-center>
                <v-flex xs10>
                    <v-card class="pt-4 pb-1 round-card">
                        <v-form v-model="valid" ref="form">
                            <!-- region Title & Header -->
                            <v-flex pt-3 pr-4 pl-4>
                                <!-- region Header -->
                                <v-flex pt-1 pb-4>
                                    <span class="subheading font-weight-medium">Create course</span>
                                </v-flex>
                                <!-- endregion -->

                                <!-- region Title -->
                                <v-flex pb-5 mb-5>
                                    <v-text-field prepend-icon="mdi-format-title" placeholder="Title" class="headline" v-model="course.title" :rules="[rules.required]" tabindex="1"></v-text-field>
                                    <v-textarea prepend-icon="mdi-file-document" rows="1" placeholder="Description" v-model="course.desc" :rules="[rules.required]" tabindex="1"></v-textarea>
                                    <v-text-field prepend-icon="mdi-link" placeholder="Link to Logo" v-model="course.courseImg" :rules="[rules.required, rules.url]" tabindex="1"></v-text-field>
                                    <v-btn text color="primary" @click="save" class="float-right caption" :disabled="course.content.length === 0">
                                        Save course
                                    </v-btn>
                                </v-flex>
                                <!-- endregion -->
                            </v-flex>
                            <!-- endregion -->

                            <v-divider></v-divider>


                            <!-- region Videos -->
                            <v-expansion-panels :value="lastPanel">
                                <div v-if="course.content.length === 0" class="width-100">

                                    <!--region Add Video/Lab Buttons -->
                                    <v-flex d-flex>
                                        <v-card flat>
                                            <!--region SHOWN CONTENT GOES HERE -->
                                            <v-card-text >
                                                <span>Press<v-icon small class="mx-1">mdi-video-vintage</v-icon> or <v-icon small class="mx-1">mdi-notebook-multiple</v-icon> to add content to the course! </span>
                                            </v-card-text>
                                            <!-- endregion -->
                                        </v-card>



                                    </v-flex>
                                    <!-- endregion -->
                                </div>
                                <div v-for="(video, videoIndex) in course.content" :key="videoIndex" class="width-100" v-else>
                                    <!--region Video -->
                                    <v-flex d-flex v-if="video.contentType === 'VIDEO'">
                                        <v-expansion-panel v-model="video.panel">
                                            <v-expansion-panel-header :disable-icon-rotate="isError(video, videoIndex) || isValid(video)">
                                                <!--region SHOWN CONTENT GOES HERE -->
                                                <span class="overline">
                                                    <v-icon class="mr-3">mdi-video</v-icon>
                                                    Video {{ videoNumber(video) }} {{ !!video.title ? '-' : '' }} <strong>{{ video.title }}</strong>
                                                </span>
                                                <template v-slot:actions>
                                                    <v-icon color="error" v-if="isError(video, videoIndex)">
                                                        mdi-alert-circle
                                                    </v-icon>
                                                    <v-icon color="success" v-else-if="isValid(video)">
                                                        mdi-check
                                                    </v-icon>
                                                    <v-icon v-else ref="icon">
                                                        mdi-chevron-down
                                                    </v-icon>
                                                </template>

                                                <!-- endregion -->
                                            </v-expansion-panel-header>
                                            <v-expansion-panel-content>
                                                <!-- region HIDDEN CONTENT GOES HERE -->
                                                <v-flex>
                                                    <v-textarea prepend-icon="mdi-format-title" class="mt-2 pt-3" placeholder="Video Title" rows="1" :ref="'video-' + videoIndex + '-title'"
                                                                single-line auto-grow v-model="video.title"
                                                                :rules="[rules.required]"
                                                    ></v-textarea>
                                                    <v-text-field prepend-icon="mdi-file-document" placeholder="Description" npi v-model="video.desc" :ref="'video-' + videoIndex + '-desc'"></v-text-field>
                                                    <v-text-field prepend-icon="mdi-link" :id="`url-${videoIndex}`" @input="clearDuration(video, videoIndex)" placeholder="URL" :rules="[rules.required, rules.url]" v-model="video.url" :ref="'video-' + videoIndex + '-url'"></v-text-field>
                                                    <v-text-field prepend-icon="mdi-timer" placeholder="Duration" v-model="video.duration" :loading="!!video.url && !video.duration" disabled :ref="'video-' + videoIndex + '-duration'"></v-text-field>
                                                    <video :id="`video-${videoIndex}`" preload="metadata" width="0" height="0" controls :src="video.url" @loadedmetadata="getDuration(videoIndex)"></video>
                                                </v-flex>

                                                <!-- endregion -->

                                                <!--region Actions -->
                                                <v-card-actions class="float-right">
                                                    <v-tooltip bottom>
                                                        <template v-slot:activator="{ on }">
                                                            <v-btn icon v-on="on" @click="removeVideo(video)"
                                                                   class="mx-1">
                                                                <v-icon color="grey">delete</v-icon>
                                                            </v-btn>
                                                        </template>
                                                        <span>Delete</span>
                                                    </v-tooltip>
                                                </v-card-actions>
                                                <!-- endregion -->
                                            </v-expansion-panel-content>
                                        </v-expansion-panel>
                                    </v-flex>
                                    <!-- endregion -->
                                    <!--region Lab -->
                                    <v-flex d-flex v-else-if="video.contentType === 'LAB'">
                                        <v-expansion-panel v-model="video.panel">
                                            <v-expansion-panel-header :disable-icon-rotate="isError(video, videoIndex) || isValid(video)">
                                                <!--region SHOWN CONTENT GOES HERE -->
                                                <span class="overline">
                                                    <v-icon class="mr-3">mdi-file-document-edit</v-icon>
                                                    Lab {{ labNumber(video) }} {{ !!video.title ? '-' : '' }} <strong>{{ video.title }}</strong>
                                                </span>
                                                <template v-slot:actions>
                                                    <v-icon color="error" v-if="isError(video, videoIndex)">
                                                        mdi-alert-circle
                                                    </v-icon>
                                                    <v-icon color="success" v-else-if="isValid(video)">
                                                        mdi-check
                                                    </v-icon>
                                                    <v-icon v-else ref="icon">
                                                        mdi-chevron-down
                                                    </v-icon>
                                                </template>

                                                <!-- endregion -->
                                            </v-expansion-panel-header>
                                            <v-expansion-panel-content>
                                                <!-- region HIDDEN CONTENT GOES HERE -->
                                                <v-flex>
                                                    <v-textarea prepend-icon="mdi-format-title" class="mt-2 pt-3" placeholder="Lab Title" rows="1" :ref="'video-' + videoIndex + '-title'"
                                                                single-line auto-grow v-model="video.title"
                                                                :rules="[rules.required]"
                                                    ></v-textarea>
                                                    <v-text-field prepend-icon="mdi-link" :id="`url-${videoIndex}`" placeholder="Link to Lab" :rules="[rules.required, rules.url]" v-model="video.url" :ref="'video-' + videoIndex + '-url'"></v-text-field>
                                                </v-flex>

                                                <!-- endregion -->

                                                <!--region Actions -->
                                                <v-card-actions class="float-right">
                                                    <v-tooltip bottom>
                                                        <template v-slot:activator="{ on }">
                                                            <v-btn icon v-on="on" @click="removeVideo(video)"
                                                                   class="mx-1">
                                                                <v-icon color="grey">delete</v-icon>
                                                            </v-btn>
                                                        </template>
                                                        <span>Delete</span>
                                                    </v-tooltip>
                                                </v-card-actions>
                                                <!-- endregion -->
                                            </v-expansion-panel-content>
                                        </v-expansion-panel>
                                    </v-flex>
                                    <!-- endregion -->
                                    <!-- region Video Divider -->
                                    <v-flex v-if="course.content.length > videoIndex + 1">
                                        <v-divider></v-divider>
                                    </v-flex>
                                    <!-- endregion -->
                                </div>
                            </v-expansion-panels>
                            <!-- endregion -->
                            <!--region Add Video -->
                            <v-flex>
                                <v-divider></v-divider>
                                <v-spacer></v-spacer>
                                <v-card-actions>
                                    <!--region Add Video -->
                                    <v-tooltip bottom>
                                        <template v-slot:activator="{ on }">
                                            <v-btn icon v-on="on" @click="addVideo()" class="ml-1">
                                                <v-icon color="grey">mdi-video-vintage</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Add video</span>
                                    </v-tooltip>
                                    <!-- endregion -->
                                    <!--region Add Lab -->
                                    <v-tooltip bottom>
                                        <template v-slot:activator="{ on }">
                                            <v-btn icon v-on="on" @click="addLab()">
                                                <v-icon color="grey">mdi-notebook-multiple</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Add lab</span>
                                    </v-tooltip>
                                    <!-- endregion -->

                                </v-card-actions>
                            </v-flex>
                            <!-- endregion -->
                        </v-form>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
        <v-container fluid shrink>
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
        </v-container>
    </v-app>
</template>

<script>
    import common from '../../common.js'
    import {mapGetters} from 'vuex'
    import {AXIOS} from "../../http-commons";

    export default {
        name: "CourseBuilder",
        data() {
            return {
                alert: {
                    active: false,
                    message: '',
                    color: undefined,
                    btnColor: undefined,
                    timeout: 0
                },
                duration: null,
                valid: false,
                path: {
                    courses: []
                },
                course: {
                    content: [],
                    tags: [],
                    title: '',
                    desc: '',
                    courseImg: '',
                    duration: {}
                },
                rules: {
                    required: value => !!value || 'Required.',
                    url: value => {
                        const pattern = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/;
                        return pattern.test(value) || 'Invalid URL.'
                    },
                    validImg: async (value) => {
                        let valid = await this.isValidImg(value).catch((error) => { return false });
                        return valid || 'Invalid image.'
                    }
                },
                verifyUrlPatterns: true
            }
        },
        methods: {
            /**
             * Clears the duration text field if the url text field is empty
             * @param {Object} video Video to modify the duration of
             * @param {string} index Index of the video to modify the properties of
             * @returns {void}
             * @public
             */
            clearDuration(video, index) {
                if (this.course.content[index].url === '') {
                    this.course.content[index].duration === ''
                }
                else {
                    document.getElementById("video-" + index).src = video.url;
                    this.getDuration(index);
                }
            },
            /**
             * Locates an HTML video element by id and then returns it's `duration` property
             * @param {string} id Id of video element<br>
             * @returns {void}
             * @public
             */
            getDuration(id) {
                let video = document.getElementById('video-' + id);
                this.course.content[id].duration = video.duration;
            },
            /**
             * Pushes a new blank video into `this.course.content`
             * @returns {void}
             * @public
             */
            addVideo() {
                this.course.content.push({
                    number: this.course.content.length + 1,
                    title: '',
                    description: '',
                    url: '',
                    duration: null,
                    panel: [0, 1],
                    contentType: "VIDEO"
                });
            },
            /**
             * Pushes a new blank lab into `this.courses.content`
             * @returns {void}
             * @public
             */
            addLab() {
                this.course.content.push({
                    number: this.course.content.length + 1,
                    title: '',
                    description: '',
                    url: '',
                    duration: null,
                    panel: [0, 1],
                    contentType: "LAB"
                })
            },
            /**
             * Removes a video from `this.course.content`, as well as any `refs` associated with it.
             * @param {Object} video Video to be removed
             * @returns {void}
             * @public
             */
            removeVideo(video) {
                let index = this.course.content.indexOf(video);
                this.course.content.splice(index, 1);

                delete this.$refs["video-" + index + "-title"];
                delete this.$refs["video-" + index + "-desc"];
                delete this.$refs["video-" + index + "-url"];
            },

            /**
             * Saves a course to the database.
             * Then saves the course to all users in the database.
             * @returns {void}
             * @public
             */
            save() {
                if (this.$refs.form.validate()) {

                    let self = this;
                    let totalDuration = 0;
                    let totalCourseDuration = 0;

                    if (this.verifyUrlPatterns) {
                        for (let i = 0; i < this.course.content.length; i++) {
                            if (this.course.content[i].duration) {
                                totalCourseDuration += this.course.content[i].duration;
                            }
                            else {
                                if (!this.course.content[i].contentType === "LAB") {
                                    self.snackbar('error', 'A video did not calculate duration properly. Please validate all video links.', 5);
                                    return;
                                }
                            }
                        }
                    }
                    else {
                        self.snackbar('warning', 'IP address is not internal. Video duration cannot be determined. This course will not be usable.')
                    }

                    this.course.duration.hours = Math.floor(totalCourseDuration / 3600);
                    this.course.duration.minutes = Math.floor(totalCourseDuration % 3600 / 60);
                    this.course.duration.seconds = Math.floor(totalCourseDuration % 3600 % 60);

                    this.path.courses.push(this.course);

                    if (this.verifyUrlPatterns) {
                        for (let i = 0; i < this.path.courses.length; i++) {
                            for (let j = 0; j < this.path.courses[i].content.length; j++) {
                                totalDuration += this.path.courses[i].content[j].duration;
                            }
                        }
                    }
                    else {
                        self.snackbar('warning', 'IP address is not internal. Video duration cannot be determined. This course will not be usable.')
                    }

                    this.path.duration.hours = Math.floor(totalDuration / 3600);
                    this.path.duration.minutes = Math.floor(totalDuration % 3600 / 60);
                    this.path.duration.seconds = Math.floor(totalDuration % 3600 % 60);

                    AXIOS.put('/api/paths', this.path)
                        .then((response) => {
                            AXIOS.get('/api/users')
                                .then((response) => {
                                    let users = response.data;

                                    if (users.length === 0) {
                                        self.push("/pathways");
                                    }
                                    else {
                                        for (let i = 0; i < users.length; i++) {
                                            if (users[i].paths) {
                                                users[i].paths[self.pathIndex].courses.push(self.course);

                                                AXIOS.post('/api/users', users[i])
                                                    .then((response) => {
                                                        if (response.data.username === self.getUser.username) {
                                                            self.$store.dispatch({
                                                                type: 'setUser',
                                                                user: response.data
                                                            }).then(() => {
                                                                self.push('/pathways')
                                                            })
                                                        }
                                                    })
                                                    .catch((error) => {
                                                        console.log(error);
                                                        self.snackbar('error', 'The course was saved, but an error occurred while updating users!', 2.5);
                                                    });
                                            }
                                        }
                                    }
                                })
                                .catch((error) => {
                                    console.log(error);
                                    self.snackbar('error', 'The course was saved, but an error occurred while getting users!', 2.5);
                                });
                        })
                        .catch((error) => {
                            console.log(error);
                            self.snackbar('error', 'An error occurred while saving to the database!', 2.5);
                        })
                }
                else {
                    this.snackbar('warning', 'Please fill out all fields!', 2.5)
                }
            },
            /**
             * Returns true if src is the link of a valid image
             * @param {string} src URL of an image
             * @returns {boolean}
             */
            async isValidImg(src) {
                let valid;
                if (this.verifyUrlPatterns) {
                    return new Promise((resolve, reject) => {
                        let img = new Image();
                        img.addEventListener('load', e => resolve(true));
                        img.addEventListener('error', () => {
                            reject(false);
                        });
                        img.src = src;
                    });
                }
                else {
                    return true;
                }

            },
            /**
             * Returns true if data entered is valid and a valid resource exists at the URL
             * @param {Object} video Object containing data entered in the form
             * @returns {boolean}
             */
            isValid(video) {
                if (video.contentType === "VIDEO" && (!!video.title && !!video.description && !!video.duration)) {
                    if (this.verifyUrlPatterns) {
                        if (video.url !== "") {
                            let valid = true;
                            let testVideo = document.createElement('video');
                            testVideo.src = video.url;

                            testVideo.onerror = function() {
                                valid = false;
                            };

                            return valid;
                        }
                    }
                }
                else if (video.contentType === 'LAB' && !!video.title && !!video.url) {
                    return true;
                }
                else if (!this.verifyUrlPatterns && !!video.title && !!video.description && !!video.url) {
                    return true;
                }

                return false

            },
            /**
             * Returns true if any fields in the form are invalid for a given video. Invalid is defined as touched/dirty or outside of `rules` constraints on the text field.
             * Also returns true if the URL does not contain a valid resource, as `video.duration` will be `NaN`.
             * @param {Object} video Object containing data entered in the form
             * @param {Number} index Index of video in `this.course.content`
             * @returns {boolean}
             */
            isError(video, index) {
                let validTitle = true;
                let validDesc = true;
                let validURL = true;
                let validDuration = true;

                if (this.$refs["video-" + index + "-title"]) validTitle = this.$refs["video-" + index + "-title"][0].validationState !== "error";
                if (this.$refs["video-" + index + "-desc"]) validDesc = this.$refs["video-" + index + "-desc"][0].validationState !== "error";
                if (this.$refs["video-" + index + "-url"]) validURL = this.$refs["video-" + index + "-url"][0].validationState !== "error";
                if (this.verifyUrlPatterns) {
                    validDuration = !!video.duration === true || !isNaN(video.duration);
                }

                return !validTitle || !validDesc || !validURL || !validDuration;
            },
            /**
             * Returns the 1-based index of a video in `filteredVideos`
             * @param {Object} lab The lab to find the index of in the filtered labs
             * @returns {Number}
             * @public
             */
            videoNumber(video) {
                let filteredVideos = this.course.content.filter((video) => video.contentType === "VIDEO")
                return filteredVideos.indexOf(video) + 1;
            },
            /**
             * Returns the 1-based index of a video in `filteredLabs`
             * @param {Object} lab The lab to find the index of in the filtered labs
             * @returns {Number}
             * @public
             */
            labNumber(lab) {
                let filteredLabs = this.course.content.filter((video) => video.contentType === 'LAB')
                return filteredLabs.indexOf(lab) + 1;
            },
            /**
             * Returns the number of video objects with `contentType === "VIDEO` equal to `true`
             * @returns {Number}
             * @public
             */
            videoCount() {
                return this.course.content.filter((video) => video.contentType === "VIDEO").length;
            },
            /**
             * Checks the client's IP address and sets `this.verifyUrlPatterns` to `false`
             * if it is not a private IP address (does not start with 10).
             * @returns {void}
             * @public
             */
            verifyIpAddress() {
                let self = this;

                fetch('https://api.ipify.org?format=json')
                    .then((response) => {
                        response.json()
                            .then((body) => {
                                if (body.ip.split(".")[0] !== "10") {
                                    self.verifyUrlPatterns = false;
                                }
                            })
                    })
                    .catch((error) => {
                        console.log(error);
                    })
            }
        },
        computed: {
            ...mapGetters([
                'getUser'
            ]),
            pathIndex() {
                return this.$route.params.id;
            },
            lastPanel() {
                return this.course.content.length - 1;
            }
            
        },
        mounted() {
            let self = this;
            this.push = common.push;
            this.log = common.log;
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

            AXIOS.get('/api/paths')
                .then((response) => {
                    let paths = response.data;
                    self.path = paths[self.pathIndex];
                })
                .catch((error) => {
                    console.log(error);
                });

            this.verifyIpAddress();
        }
    }
</script>

<style scoped>
    .width-100 {
        width: 100%!important;
    }
    .float-right {
        float: right;
    }
</style>

<docs>
    Used for creating new courses and associating content with them.

    ---
</docs>