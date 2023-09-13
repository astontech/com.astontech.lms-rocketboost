<template>
    <v-app>
        <v-container ma-0>
            <v-flex pb-2 d-flex>
                <v-flex d-inline>
                    <span class="title font-weight-light">Course Editor</span>
                    <v-subheader class="caption pa-0">{{ videoCount() }} video{{ videoCount !== 1 ? 's' : '' }} on 1
                        course
                    </v-subheader>
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
                                    <v-text-field prepend-icon="mdi-format-title" placeholder="Title" class="headline"
                                                  v-model="course.title" :rules="[rules.required]"
                                                  tabindex="1"></v-text-field>
                                    <v-textarea prepend-icon="mdi-file-document" rows="1" placeholder="Description"
                                                v-model="course.desc"
                                                :rules="[rules.required]" tabindex="1"></v-textarea>
                                    <v-text-field prepend-icon="mdi-link" placeholder="Link to Logo"
                                                  v-model="course.courseImg"
                                                  :rules="[rules.required, rules.url]" tabindex="1"></v-text-field>
                                    <v-btn text color="primary" @click="save" class="float-right caption">
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
                                            <v-card-text>
                                                <span>
                                                  Press<v-icon small class="mx-1">mdi-video-vintage</v-icon> or <v-icon
                                                    small class="mx-1">mdi-notebook-multiple</v-icon> to add content to the course! </span>
                                            </v-card-text>
                                            <!-- endregion -->
                                        </v-card>


                                    </v-flex>
                                    <!-- endregion -->
                                </div>
                                <draggable :list="course.content"
                                           :disabled="!enabled"
                                           class="list-group width-100"
                                           ghost-class="ghost"
                                           @start="dragging = true"
                                           @end="dragging = false"
                                           v-else>
                                    <transition-group>
                                        <div v-for="(content, videoIndex) in course.content" :key="videoIndex"
                                             class="width-100">
                                            <!--region Video -->
                                            <v-flex d-flex v-if="content.contentType === 'VIDEO'">
                                                <v-expansion-panel v-model="content.panel">
                                                    <v-expansion-panel-header
                                                        :disable-icon-rotate="isError(content, videoIndex) || isValid(content)">
                                                        <!--region SHOWN CONTENT GOES HERE -->
                                                        <span class="overline">
                                                    <v-icon class="mr-3">mdi-video</v-icon>
                                                    Video {{ videoNumber(content) }} {{
                                                                !!content.title ? '-' : ''
                                                            }} <strong>{{ content.title }}</strong>
                                                </span>
                                                        <template v-slot:actions>
                                                            <v-icon color="error" v-if="isError(content, videoIndex)">
                                                                mdi-alert-circle
                                                            </v-icon>
                                                            <v-icon color="success" v-else-if="isValid(content)">
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
                                                            <v-textarea prepend-icon="mdi-format-title"
                                                                        class="mt-2 pt-3" placeholder="Video Title"
                                                                        rows="1"
                                                                        :ref="'content-' + videoIndex + '-title'"
                                                                        single-line auto-grow v-model="content.title"
                                                                        :rules="[rules.required]"
                                                            ></v-textarea>
                                                            <v-text-field prepend-icon="mdi-file-document"
                                                                          placeholder="Description"
                                                                          :rules="[rules.required]"
                                                                          v-model="content.description"
                                                                          :ref="'content-' + videoIndex + '-desc'"></v-text-field>
                                                            <v-text-field prepend-icon="mdi-link"
                                                                          :id="`url-${videoIndex}`"
                                                                          @input="clearDuration(content, videoIndex)"
                                                                          placeholder="URL"
                                                                          :rules="[rules.required, rules.url]"
                                                                          v-model="content.url"
                                                                          :ref="'content-' + videoIndex + '-url'"></v-text-field>
                                                            <v-text-field prepend-icon="mdi-timer"
                                                                          placeholder="Duration"
                                                                          v-model="content.duration"
                                                                          :loading="!!content.url && !content.duration"
                                                                          disabled
                                                                          :ref="'content-' + videoIndex + '-duration'"></v-text-field>
                                                            <video :id="`content-${videoIndex}`" preload="metadata"
                                                                   width="0" height="0" controls
                                                                   :src="content.url"
                                                                   @loadedmetadata="getDuration(videoIndex)"></video>
                                                        </v-flex>

                                                        <!-- endregion -->

                                                        <!--region Actions -->
                                                        <v-card-actions class="float-right">
                                                            <v-tooltip bottom>
                                                                <template v-slot:activator="{ on }">
                                                                    <v-btn icon v-on="on" @click="removeVideo(content)"
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
                                            <v-flex d-flex v-else-if="content.contentType === 'LAB'">
                                                <v-expansion-panel v-model="content.panel">
                                                    <v-expansion-panel-header
                                                        :disable-icon-rotate="isError(content, videoIndex) || isValid(content)">
                                                        <!--region SHOWN CONTENT GOES HERE -->
                                                        <span class="overline">
                                                    <v-icon class="mr-3">mdi-file-document-edit</v-icon>
                                                    Lab {{ labNumber(content) }} {{
                                                                !!content.title ? '-' : ''
                                                            }} <strong>{{ content.title }}</strong>
                                                </span>
                                                        <template v-slot:actions>
                                                            <v-icon color="error" v-if="isError(content, videoIndex)">
                                                                mdi-alert-circle
                                                            </v-icon>
                                                            <!--                              <v-icon color="success" v-else-if="isValid(content)">-->
                                                            <!--                                mdi-check-->
                                                            <!--                              </v-icon>-->
                                                            <v-icon v-else ref="icon">
                                                                mdi-chevron-down
                                                            </v-icon>
                                                        </template>

                                                        <!-- endregion -->
                                                    </v-expansion-panel-header>
                                                    <v-expansion-panel-content>
                                                        <!-- region HIDDEN CONTENT GOES HERE -->
                                                        <v-flex>
                                                            <v-textarea prepend-icon="mdi-format-title"
                                                                        class="mt-2 pt-3" placeholder="Lab Title"
                                                                        rows="1"
                                                                        :ref="'content-' + videoIndex + '-title'"
                                                                        single-line auto-grow v-model="content.title"
                                                                        :rules="[rules.required]"
                                                            ></v-textarea>
                                                            <v-text-field prepend-icon="mdi-link"
                                                                          :id="`url-${videoIndex}`"
                                                                          placeholder="Link to Lab"
                                                                          :rules="[rules.required, rules.url]"
                                                                          v-model="content.url"
                                                                          :ref="'content-' + videoIndex + '-url'"></v-text-field>
                                                        </v-flex>

                                                        <!-- endregion -->

                                                        <!--region Actions -->
                                                        <v-card-actions class="float-right">
                                                            <v-tooltip bottom>
                                                                <template v-slot:activator="{ on }">
                                                                    <v-btn icon v-on="on" @click="removeVideo(content)"
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
                                            <v-flex d-flex v-else>
                                                <v-expansion-panel v-model="content.panel">
                                                    <v-expansion-panel-header
                                                        :disable-icon-rotate="isError(content, videoIndex) || isValid(content)">
                                                        <!--region SHOWN CONTENT GOES HERE -->
                                                        <span class="overline">
                                                    <v-icon class="mr-3">mdi-file-document-edit</v-icon>
                                                    Quiz {{ quizNumber(content) }} {{
                                                                !!content.title ? '-' : ''
                                                            }} <strong>{{ content.title }}</strong>
                                                </span>
                                                        <template v-slot:actions>
                                                            <v-icon color="error" v-if="isError(content, videoIndex)">
                                                                mdi-alert-circle
                                                            </v-icon>
                                                            <v-icon v-else ref="icon">
                                                                mdi-chevron-down
                                                            </v-icon>
                                                        </template>

                                                        <!-- endregion -->
                                                    </v-expansion-panel-header>
                                                    <v-expansion-panel-content>
                                                        <!-- region HIDDEN CONTENT GOES HERE -->
                                                        <v-flex style="display: grid; grid-template-columns: 1fr 2fr">
                                                            <v-card outlined>
                                                                <v-list style="height: 250px; overflow-y: auto">
                                                                    <v-subheader>Quizzes</v-subheader>
                                                                    <v-list-item-group
                                                                        v-model="course.content[videoIndex]"
                                                                        color="primary">
                                                                        <v-list-item
                                                                            v-for="(quiz, i) in quizzes"
                                                                            :key="i"
                                                                            :value="quiz"
                                                                        >
                                                                            <v-list-item-content>
                                                                                <v-list-item-title
                                                                                    v-text="quiz.title"></v-list-item-title>
                                                                            </v-list-item-content>
                                                                        </v-list-item>
                                                                    </v-list-item-group>
                                                                </v-list>
                                                            </v-card>
                                                            <v-textarea prepend-icon="mdi-format-title"
                                                                        style="width: 75%"
                                                                        class="mt-2 ml-5 pt-3" placeholder="Quiz Title"
                                                                        rows="1"
                                                                        :ref="'content-' + videoIndex + '-title'"
                                                                        single-line auto-grow
                                                                        v-model="course.content[videoIndex].title"
                                                                        :rules="[rules.required]"
                                                            ></v-textarea>
                                                        </v-flex>

                                                        <!-- endregion -->

                                                        <!--region Actions -->
                                                        <v-card-actions class="float-right">
                                                            <v-tooltip bottom>
                                                                <template v-slot:activator="{ on }">
                                                                    <v-btn icon v-on="on" @click="removeVideo(content)"
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

                                            <!-- region Video Divider -->
                                            <v-flex v-if="course.content.length > videoIndex + 1">
                                                <v-divider></v-divider>
                                            </v-flex>
                                            <!-- endregion -->
                                        </div>
                                    </transition-group>
                                </draggable>
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
                                            <v-btn icon v-on="on" @click="addContent('VIDEO')" class="ml-1">
                                                <v-icon color="grey">mdi-video-vintage</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Add video</span>
                                    </v-tooltip>
                                    <!-- endregion -->
                                    <!--region Add Lab -->
                                    <v-tooltip bottom>
                                        <template v-slot:activator="{ on }">
                                            <v-btn icon v-on="on" @click="addContent('LAB')">
                                                <v-icon color="grey">mdi-notebook-multiple</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Add lab</span>
                                    </v-tooltip>
                                    <!-- endregion -->
                                    <!--region Add Quiz -->
                                    <v-tooltip bottom>
                                        <template v-slot:activator="{ on }">
                                            <v-btn icon v-on="on" @click="addContent('QUIZ')">
                                                <v-icon color="grey">mdi-file-question</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Add Quiz</span>
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
import {AXIOS} from "@/http-commons";
import draggable from 'vuedraggable';

export default {
    name: "CourseEditor",
    components: {
        draggable
    },
    data() {
        return {
            quizzes: [],
            enabled: true,
            dragging: false,
            alert: {
                active: false,
                message: '',
                color: undefined,
                btnColor: undefined,
                timeout: 0
            },
            duration: null,
            valid: false,
            loading: false,
            path: {
                courses: []
            },
            course: {
                content: [],
                tags: [],
                title: '',
                description: '',
                courseImg: '',
                duration: {}
            },
            originalCourse: {},
            rules: {
                required: value => !!value || 'Required.',
                url: value => {
                    const pattern = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/;
                    return pattern.test(value) || 'Invalid URL.'
                },
                validImg: async (value) => {
                    let valid = await this.isValidImg(value).catch((error) => {
                        return false
                    });
                    return valid || 'Invalid image.'
                }
            },
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
        clearDuration(content, index) {
            if (this.course.content[index].url === '') {
                this.course.content[index].duration === ''
            } else {
                document.getElementById("content-" + index).src = content.url;
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
            let content = document.getElementById('content-' + id);
            this.course.content[id].duration = content.duration;
        },
        /**
         * Pushes a new blank video into `this.course.content`
         * @returns {void}
         * @public
         */
        addContent(contentType) {
            if (contentType === 'VIDEO' || contentType === 'LAB') {
                this.course.content.push({
                    number: this.course.content.length + 1,
                    title: '',
                    description: '',
                    url: '',
                    duration: null,
                    panel: [0, 1],
                    contentType: contentType
                });
            } else {
                // push empty string to be replaced by string id
                this.course.content.push({
                    questions: [
                        {
                            options: [''],
                            questionType: '',
                            title: '',
                            panel: [0, 1],
                            answers: [],
                            selected: [],
                            tags: []
                        },
                    ],
                    tags: [],
                    title: '',
                    description: ''
                });
            }
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

            delete this.$refs["content-" + index + "-title"];
            delete this.$refs["content-" + index + "-desc"];
            delete this.$refs["content-" + index + "-url"];
        },
        /**
         * Processes changes made to the course, and then performs the necessary
         * operations to make the course the same in all user accounts in the database.
         * @returns {void}
         * @public
         */
        save() {
            if (this.$refs.form.validate()) {

                let self = this;

                this.setPathAndCourseDuration();

                AXIOS.put('/api/paths', this.path)
                    .then((response) => {
                        AXIOS.get('/api/users')
                            .then((response) => {
                                let users = response.data;

                                if (users.length === 0) {
                                    self.push('/pathways');
                                } else {
                                    for (let i = 0; i < users.length; i++) {
                                        if (users[i].paths) {
                                            let sameVideos = [];
                                            let newVideos = [];
                                            let localVideo = null;
                                            let userVideo = null;
                                            let deleted = true;

                                            //region Update course identifiers (title/desc)
                                            // This course located inside of the user's profile
                                            let userCourse = users[i].paths[self.pathIndex].courses[self.courseIndex];
                                            // The list of content located inside of this course in the user's profile
                                            let userVideos = userCourse.content;

                                            userCourse.title = this.course.title; // Replaces stored Course title with (potentially new) Course title
                                            userCourse.desc = this.course.desc; // Replaces stored Course desc with (potentially new) Course desc
                                            userCourse.duration = this.course.duration; // Replaces stored Course duration with new Course duration

                                            //endregion

                                            //region Filter new and existing content into different arrays
                                            // For each video on the frontend
                                            for (let j = 0; j < this.path.courses[self.courseIndex].content.length; j++) {
                                                localVideo = this.path.courses[self.courseIndex].content[j];
                                                // For each video that exists in the user's profile
                                                for (let k = 0; k < userVideos.length; k++) {
                                                    userVideo = userVideos[k];

                                                    //region Add new content to newVideos array
                                                    // Videos that haven't gone to the database yet will lack an id parameter
                                                    if (localVideo.hasOwnProperty("id") === false) {
                                                        // Add new video to array if it isn't already in the array
                                                        if (!newVideos.includes(localVideo)) {
                                                            newVideos.push(localVideo);
                                                        }
                                                    }
                                                        //endregion

                                                        //region Add videos that exist on front & backend to sameVideos array
                                                    // Videos that are not new or deleted will have the same id on the frontend and backend
                                                    else if (localVideo.id === userVideo.id) {
                                                        // Add existing video to array if it isn't already in the array
                                                        if (!sameVideos.includes(localVideo)) {
                                                            sameVideos.push(localVideo);
                                                        }
                                                    }
                                                    //endregion

                                                }

                                            }
                                            //endregion

                                            //region Update fields in existing videos that may or may not have been updated
                                            for (let j = 0; j < userVideos.length; j++) {
                                                for (let k = 0; k < sameVideos.length; k++) {
                                                    if (userVideos[j].url !== sameVideos[k].url) {
                                                        userVideos[j].completed = false;
                                                    }

                                                    userVideos[j].title = sameVideos[k].title;
                                                    userVideos[j].description = sameVideos[k].description;
                                                    userVideos[j].url = sameVideos[k].url;
                                                    userVideos[j].duration = sameVideos[k].duration;
                                                }
                                            }
                                            //endregion

                                            //region Add new videos
                                            for (let j = 0; j < newVideos.length; j++) {
                                                userVideos.push(newVideos[i]);
                                            }
                                            //endregion

                                            //region Remove deleted videos
                                            for (let j = 0; j < userVideos.length; j++) {
                                                for (let k = 0; k < this.originalCourse.content.length; k++) {
                                                    if (userVideos[j].id === this.originalCourse.content[k].id) {
                                                        deleted = false;
                                                    }
                                                }
                                                if (deleted === true) {
                                                    userVideos = userVideos.filter((video) => video.id !== userVideos[j].id);
                                                }
                                                deleted = false;
                                            }
                                            users[i].paths[self.pathIndex].courses[self.courseIndex].content = userVideos;

                                            //endregion

                                            AXIOS.post('/api/users', users[i])
                                                .then((response) => {
                                                    if (response.data.username === self.getUser.username) {
                                                        self.$store.dispatch({
                                                            type: 'setUser',
                                                            user: response.data
                                                        }).then(() => {
                                                            this.snackbar("success", "The course was successfully saved!", 2.5);
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
            } else {
                this.snackbar('warning', 'Please fill out all fields!', 2.5)
            }
        },
        /**
         * Updates the duration of a course
         * @returns {void}
         * @public
         */
        setPathAndCourseDuration() {
            let totalDuration = 0;
            let totalCourseDuration = 0;

            for (let i = 0; i < this.course.content.length; i++) {
                if (this.course.content[i].duration) {
                    totalCourseDuration += this.course.content[i].duration;
                }
            }

            this.course.duration.hours = Math.floor(totalCourseDuration / 3600);
            this.course.duration.minutes = Math.floor(totalCourseDuration % 3600 / 60);
            this.course.duration.seconds = Math.floor(totalCourseDuration % 3600 % 60);

            for (let i = 0; i < this.path.courses.length; i++) {
                for (let j = 0; j < this.path.courses[i].content.length; j++) {
                    if (!isNaN(this.path.courses[i].content[j].duration))
                        totalDuration += this.path.courses[i].content[j].duration;
                }
            }

            this.path.duration.hours = Math.floor(totalDuration / 3600);
            this.path.duration.minutes = Math.floor(totalDuration % 3600 / 60);
            this.path.duration.seconds = Math.floor(totalDuration % 3600 % 60);
        },
        /**
         * Returns true if src is the link of a valid image
         * @param {string} src URL of an image
         * @returns {boolean}
         */
        async isValidImg(src) {
            if (this.verifyUrlPatterns) {
                return new Promise((resolve, reject) => {
                    let img = new Image();
                    img.addEventListener('load', e => resolve(true));
                    img.addEventListener('error', () => {
                        reject(false);
                    });
                    img.src = src;
                });
            } else {
                return true;
            }

        },
        /**
         * Returns true if data entered is valid and a valid resource exists at the URL
         * @param {Object} video Object containing data entered in the form
         * @returns {boolean}
         */
        isValid(video) {
            if (video.contentType === 'VIDEO' && (!!video.title && !!video.description && !!video.duration)) {
                if (this.verifyUrlPatterns) {
                    if (video.url !== "") {
                        let valid = true;
                        let testVideo = document.createElement('content');
                        testVideo.src = video.url;

                        testVideo.onerror = function () {
                            valid = false;
                        };

                        return valid;
                    }
                }
            } else if (video.contentType === 'LAB' && !!video.title && !!video.url) {
                return true;
            } else if (!this.verifyUrlPatterns && !!video.title && !!video.description && !!video.url) {
                return true;
            }

            return false

        },
        /**
         * Returns true if any fields in the form are invalid for a given video. Invalid is defined as touched/dirty or outside of `rules` constraints on the text field.
         * Also returns true if the URL does not contain a valid resource, as `video.duration` will be `NaN`.
         * @param {Object} video Object containing data entered in the form
         * @param {Number} index Index of video in `this.course.videos`
         * @returns {boolean}
         */
        isError(video, index) {
            let validTitle = true;
            let validDesc = true;
            let validURL = true;
            let validDuration = true;

            // if (this.$refs["content-" + index + "-title"]) validTitle = this.$refs["content-" + index + "-title"][0].validationState !== "error";
            // if (this.$refs["content-" + index + "-desc"]) validDesc = this.$refs["content-" + index + "-desc"][0].validationState !== "error";
            // if (this.$refs["content-" + index + "-url"]) validURL = this.$refs["content-" + index + "-url"][0].validationState !== "error";
            if (this.verifyUrlPatterns) {
                validDuration = !!video.duration === true || !isNaN(video.duration);
            }

            // return !validTitle || !validDesc || !validURL || !validDuration;
            return !validDuration;
        },
        /**
         * Returns the 1-based index of a video in `filteredVideos`
         * @param {Object} lab The lab to find the index of in the filtered labs
         * @returns {Number}
         * @public
         */
        videoNumber(video) {
            let filteredVideos = this.course.content.filter((c) => c.contentType === 'VIDEO')
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
         * Returns the 1-based index of a quiz in `filteredQuiz`
         * @param {Object} quiz The quiz to find the index of in the filtered quiz
         * @returns {Number}
         * @public
         */
        quizNumber(quiz) {
            let filteredQuiz = this.course.content.filter((q) => {
                return q.contentType === 'QUIZ' || typeof q === 'string';
            });
            return filteredQuiz.indexOf(quiz) + 1;
        },
        /**
         * Returns the number of video objects with `isVideo` equal to `true`
         * @returns {Number}
         * @public
         */
        videoCount() {
            return this.course.content.filter((c) => c.contentType === 'VIDEO').length;
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
            return this.$route.params.pathId;
        },
        courseIndex() {
            return this.$route.params.courseId;
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

        this.loading = true;
        AXIOS.get('/api/paths')
            .then((response) => {
                let paths = response.data;
                self.path = paths[self.pathIndex];

                if (paths[self.pathIndex].courses.length) {
                    self.course = paths[self.pathIndex].courses[self.courseIndex];
                    Object.assign(self.originalCourse, paths[self.pathIndex].courses[self.courseIndex]);
                }
            })
            .catch((error) => {
                console.log(error);
            })
            .finally(() => {
                self.loading = false;
            });

        AXIOS.get('/api/quizzes/')
            .then((response) => {
                this.quizzes = response.data;
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
    width: 100% !important;
}

.float-right {
    float: right;
}

.ghost {
    opacity: 0.5;
    background: #c8ebfb;
}

.list-group {
    min-height: 20px;
}
</style>

<docs>
Used for editing courses. Uses the same interface as CourseBuilder, but pre-populates the fields with the details from
the provided course.

---
</docs>