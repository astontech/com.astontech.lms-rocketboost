<template>
    <div>
        <v-snackbar
            v-model="alert.active"
            top
            :color="alert.color"
            :timeout="0"
            absolute
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
        <v-overlay
            :absolute=false
            :value="videoOverlay"
            :opacity=0.8
        >
            <span style="padding-right: 50px">
                Loading Next Video...
            </span>

            <v-progress-circular
                indeterminate
                size="50"
                :width="7"
                color="white"
            ></v-progress-circular>
        </v-overlay>
        <v-overlay
            :absolute=false
            :opacity=0.8
            :value="labOverlay.active"
            :z-index=5
        >
            <v-btn small icon
                   dark @click="labOverlay.active = false">
                <v-icon color="primary">
                    mdi-close
                </v-icon>
            </v-btn>
            <v-btn
                color="primary"
                @click="openNewTab(labOverlayUrl)"
            >
                DOWNLOAD {{ labOverlayTitle }}
            </v-btn>
        </v-overlay>
        <v-overlay
            :absolute=false
            :opacity=0.8
            :value="endOfCourseOverlay"
            :z-index=5
        >
            <span style="padding-right: 50px">
                Congratulations! Course Completed...
            </span>
            <v-btn
                color="primary"
                @click="toVideo(endOfCourseOverlayNextCourse, 0), endOfCourseOverlay = false"
            >
                BEGIN NEXT COURSE
            </v-btn>
        </v-overlay>
        <v-overlay
            :absolute=false
            :opacity=0.8
            :value="endOfPathOverlay"
            :z-index=5
        >
            <v-btn
                color="primary"
                @click="endOfPathOverlay = false"
            >
                Congratulations! Path Completed!!!
            </v-btn>
        </v-overlay>
        <v-dialog
            v-model="controlDialog"
            width="500"
        >
            <v-card>

                <v-toolbar flat>
                    <v-toolbar-title>Player Controls</v-toolbar-title>
                </v-toolbar>
                <v-divider></v-divider>

                <v-card-text>
                    <v-flex v-for="control in controls" :key="control.button">
                        <div class="overline py-2">
                            <span :class="{ 'title' : isArrow(control.button) }"><b>{{ control.button }}</b></span>
                            <v-spacer></v-spacer>
                            <span>{{ control.use }}</span>
                        </div>
                    </v-flex>
                </v-card-text>

                <v-divider></v-divider>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                        color="primary"
                        text
                        class="caption"
                        @click="controlDialog = false"
                    >
                        Close
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog v-model="noteDialog" persistent max-width="400">
            <v-card>
                <v-card-title class="headline normal-height">Add a Note</v-card-title>
                <v-card-text>
                    <v-form ref="form">
                        <v-textarea label="Note" rows="3" row-height="30" auto-grow id="video-notes" outlined
                                    v-model="note"
                                    :rules="[rules.required]"></v-textarea>
                        <v-checkbox id="timestamp-checkbox" color="primary"
                                    label="Add timestamp to note"
                                    v-model="timestamp"></v-checkbox>
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" class="caption" text @click="close()">Cancel</v-btn>
                    <v-btn color="primary" class="caption" text @click="saveNote()">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog v-model="linkDialog" persistent max-width="550">
            <v-card>
                <v-card-title class="headline normal-height">Insert a Link</v-card-title>
                <v-card-text>
                    <v-form ref="linkForm">
                        <v-text-field label="URL" id="link-url" :rules="[rules.required, rules.url]"
                                      v-model="link.url"></v-text-field>
                        <v-text-field label="Link Text" id="link-text" :rules="[rules.required]"
                                      v-model="link.text"></v-text-field>
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" class="caption" text @click="closeLinkDialog()">Cancel</v-btn>
                    <v-btn color="primary" class="caption" text @click="addLink()">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog v-model="additionalMaterialAlert" persistent max-width="550">
            <v-card>
                <v-card-title class="headline normal-height">
                    <v-icon>mdi-alert</v-icon>&nbsp;Attention&nbsp;<v-icon>mdi-alert</v-icon>
                </v-card-title>
                <v-card-text>This video has additional materials.</v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" class="caption" text @click="additionalMaterialAlert = false">Ok</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <div class="page-layout"
             @mouseenter="checkcheck"
             @mouseleave="hovered = false">

            <!--VIDEO PLAYER SECTION-->
            <div class="video-player-section right-border">
                <div class="button-grid">
                    <!-- BACK BUTTON -->
                    <v-fade-transition>
                        <div :style="{visibility: hovered ? 'visible' : 'hidden'}">
                            <v-tooltip right>
                                <template v-slot:activator="{on}">
                                    <v-btn small icon
                                           dark @click="back"
                                           v-on="on">
                                        <v-icon color="primary">
                                            mdi-arrow-left
                                        </v-icon>
                                    </v-btn>
                                </template>
                                <span>Back</span>
                            </v-tooltip>
                        </div>
                    </v-fade-transition>

                    <!-- MENU BUTTON -->
                    <div class="menu-button-placement">
                        <v-fade-transition>
                            <div class="menu-bar" :style="{visibility: hovered ? 'visible' : 'hidden'}">
                                <v-tooltip left>
                                    <template v-slot:activator="{on}">
                                        <v-btn small icon
                                               dark @click="sidebarOpen = !sidebarOpen" v-on="on">
                                            <v-icon color="primary">
                                                mdi-menu
                                            </v-icon>
                                        </v-btn>
                                    </template>
                                    <span>Sidebar</span>
                                </v-tooltip>
                            </div>
                        </v-fade-transition>
                    </div>
                </div>

                <!-- VIDEO PLAYER -->
                <video invertTime controls id="player"
                       v-show="!materialsDialog && currentVideo.contentType === 'VIDEO'"></video>
                <v-overlay
                    :absolute="overlay.absolute"
                    :opacity="overlay.opacity"
                    :value="overlay.overlay"
                    :z-index="overlay.zIndex"
                >
                    <v-card>
                        <v-card-title>BOOOM NEXT!</v-card-title>
                        <v-card-text>Great Job Developer! Click Next To Continue Your Learning!</v-card-text>
                        <v-btn class="mb-2 ml-2" color="primary" @click="playNextVideo">Next</v-btn>
                    </v-card>
                </v-overlay>
            </div>

            <!--VIDEO NAV BAR SECTION-->
            <div class="video-bar" :style="{display: showSidebar}">
                <!-- VIDEO NAV BAR TITLE BOX -->
                <v-layout>
                    <v-card flat class="video-nav-bar-header">
                        <!-- TITLE -->
                        <h4 class="current-video-title">
                            {{ currentVideo.title }}
                        </h4>

                        <!-- BUTTONS -->
                        <div class="nav-tools">
                            <v-tooltip>
                                <template v-slot:activator="{ on }">
                                    <v-btn icon
                                           small
                                           class="float-right"
                                           v-on="on"
                                           @click="noteDialog = !noteDialog">
                                        <v-icon>mdi-notebook-outline</v-icon>
                                    </v-btn>
                                </template>
                                <span>Add a Note</span>
                            </v-tooltip>
                            <v-tooltip>
                                <template v-slot:activator="{ on }">
                                    <v-btn icon
                                           small
                                           class="float-right"
                                           v-on="on"
                                           @click="controlDialog = !controlDialog">
                                        <v-icon>mdi-remote-tv</v-icon>
                                    </v-btn>
                                </template>
                                <span>Player Controls</span>
                            </v-tooltip>
                        </div>
                    </v-card>
                </v-layout>

                <!-- TAB BUTTONS -->
                <v-tabs color="primary"
                        v-model="tab"
                        grow
                        centered>
                    <v-tabs-slider></v-tabs-slider>

                    <v-tab v-for="tabIndex in tabs" :key="tabIndex" :href="`#tab-${tabIndex}`" :ripple="false">
                        <v-icon v-if="tabIndex == 2 && hasAdditionalMaterials()">mdi-alert</v-icon>
                        <span style="font-size: 12px">{{ tabText[tabIndex - 1] }}</span>
                        <v-icon v-if="tabIndex == 2 && hasAdditionalMaterials()">mdi-alert</v-icon>
                    </v-tab>
                </v-tabs>

                <!-- SECTION AND VIDEO LIST -->
                <v-tabs-items v-model="tab"
                              class="overflow-y-auto"
                              :style="{height: videoNavHeight + 'px'}"
                              v-scroll.self>
                    <v-tab-item value="tab-1">
                        <v-expansion-panels accordion multiple v-model="panels">
                            <v-expansion-panel v-for="(course, courseIndex) in currentPath.courses"
                                               :key="courseIndex"
                                               class="mt-0"
                                               style="border-radius: unset; overflow-x: hidden;">
                                <v-expansion-panel-header class="course-expansion-panel">
                                    <v-flex xs2>
                                        <v-progress-circular color="primary"
                                                             :value="getCourseProgress(course, courseIndex)" size="24"
                                                             class="float-left mx-4"
                                                             v-if="getCourseProgress(course, courseIndex) < 100">{{
                                                courseIndex + 1
                                            }}
                                        </v-progress-circular>
                                        <v-progress-circular color="success" value="100" size="24"
                                                             class="float-left mx-4" v-else>
                                            <v-icon small color="success" style="position: relative; bottom: 1px;">
                                                mdi-check-bold
                                            </v-icon>
                                        </v-progress-circular>
                                    </v-flex>


                                    <v-flex d-block>
                                        <v-card-title class="body-2 font-weight-medium pa-0">{{ course.title }}
                                        </v-card-title>
                                        <v-subheader class="pa-0 caption">
                                            <v-icon size="12" class="pr-1">
                                                mdi-alarm
                                            </v-icon>
                                            {{ formatTotalDuration(course) }}
                                        </v-subheader>
                                    </v-flex>
                                </v-expansion-panel-header>
                                <v-expansion-panel-content
                                    style="overflow-x: hidden;">
                                    <v-timeline :align-top="alignTop"
                                                :dense="dense" style="position: relative; right: -5px;"
                                                class="pt-4">
                                        <a v-for="(content, videoIndex) in course.content"
                                           :key="videoIndex"
                                           @click="processClick(courseIndex, videoIndex)">
                                            <v-timeline-item small
                                                             class="pb-4"
                                                             fill-dot
                                                             :color="getColor(getIcon(courseIndex, videoIndex))"
                                                             v-if="content.contentType === 'VIDEO'">
                                                <template v-slot:icon>
                                                    <v-icon :color="darkMode ? '#000' : '#fff'">
                                                        {{ getIcon(courseIndex, videoIndex) }}
                                                    </v-icon>
                                                </template>
                                                <v-flex d-flex>
                                                    <span class="caption font-weight-medium">
                                                        {{ content.title }}
                                                    </span>
                                                    <v-spacer></v-spacer>
                                                    <span class="caption font-weight-medium">
                                                        {{ formatDuration(content.duration) }}
                                                    </span>
                                                    <v-spacer style="max-width: 50px;"></v-spacer>
                                                </v-flex>
                                            </v-timeline-item>
                                            <v-timeline-item small
                                                             class="pb-4"
                                                             fill-dot
                                                             :color="getColor(getIcon(courseIndex, videoIndex))"
                                                             v-else>
                                                <template v-slot:icon>
                                                    <v-icon :color="darkMode ? '#000' : '#fff'">
                                                        {{ getIcon(courseIndex, videoIndex) }}
                                                    </v-icon>
                                                </template>
                                                <v-flex d-flex>
                                                    <span class="caption font-weight-medium">
                                                        {{ content.title }}
                                                    </span>
                                                    <v-spacer></v-spacer>
                                                    <span class="caption font-weight-medium">
                                                        {{ content.contentType }}
                                                    </span>
                                                    <v-spacer style="max-width: 50px;"></v-spacer>
                                                </v-flex>
                                            </v-timeline-item>
                                        </a>
                                    </v-timeline>

                                </v-expansion-panel-content>
                            </v-expansion-panel>
                        </v-expansion-panels>
                    </v-tab-item>
                    <v-tab-item value="tab-2">
                        <v-container>
                            <v-layout column align-content-center justify-center>
                                <v-flex shrink>
                                    <v-dialog v-model="materialsDialog" fullscreen hide-overlay
                                              transition="dialog-bottom-transition">
                                        <template v-slot:activator="{ on }">
                                            <v-btn icon x-small v-if="isAdmin" v-on="on" class="float-right">
                                                <v-icon>mdi-pencil</v-icon>
                                            </v-btn>
                                        </template>
                                        <v-card>
                                            <v-toolbar dark color="primary">
                                                <v-btn icon dark @click="materialsDialog = false" class="ml-1">
                                                    <v-icon>close</v-icon>
                                                </v-btn>
                                                <v-toolbar-title>Editing Additional Materials</v-toolbar-title>
                                                <v-spacer></v-spacer>
                                                <v-toolbar-items>
                                                    <v-btn dark text class="caption"
                                                           @click="saveAdditionalMaterial()">Save
                                                    </v-btn>
                                                </v-toolbar-items>
                                            </v-toolbar>
                                            <v-container fluid fill-height>
                                                <v-layout fill-height>
                                                    <v-flex xs9 pr-4>
                                                        <v-flex mb-2>
                                                            <v-btn-toggle class="ma-2">
                                                                <v-tooltip bottom
                                                                           v-for="button in headerButtons"
                                                                           :key="button.icon">
                                                                    <template v-slot:activator="{ on }">
                                                                        <v-btn text @click="button.operation()"
                                                                               v-on="on">
                                                                            <v-icon>{{ button.icon }}</v-icon>
                                                                        </v-btn>
                                                                    </template>
                                                                    <span>{{ button.tooltip }}</span>
                                                                </v-tooltip>
                                                            </v-btn-toggle>
                                                            <v-btn-toggle class="ma-2">
                                                                <v-tooltip bottom
                                                                           v-for="button in formattingButtons"
                                                                           :key="button.icon">
                                                                    <template v-slot:activator="{ on }">
                                                                        <v-btn text @click="button.operation()"
                                                                               v-on="on">
                                                                            <v-icon>{{ button.icon }}</v-icon>
                                                                        </v-btn>
                                                                    </template>
                                                                    <span>{{ button.tooltip }}</span>
                                                                </v-tooltip>
                                                            </v-btn-toggle>
                                                            <v-btn-toggle class="ma-2">
                                                                <v-tooltip bottom
                                                                           v-for="button in otherButtons"
                                                                           :key="button.icon">
                                                                    <template v-slot:activator="{ on }">
                                                                        <v-btn text @click="button.operation()"
                                                                               v-on="on">
                                                                            <v-icon>{{ button.icon }}</v-icon>
                                                                        </v-btn>
                                                                    </template>
                                                                    <span>{{ button.tooltip }}</span>
                                                                </v-tooltip>
                                                            </v-btn-toggle>
                                                        </v-flex>
                                                        <v-textarea outlined
                                                                    label="Video Information"
                                                                    id="video-materials"
                                                                    @mouseup.stop="getSelectedWord()"
                                                                    color="primary"
                                                                    v-model="materialsMarkdown"
                                                                    @keyup="markdownToHtml(materialsMarkdown)"
                                                                    rows="10"
                                                                    row-height="25"
                                                        ></v-textarea>
                                                        <small class="primary--text">
                                                            <a @click="openNewTab('https://bit.ly/2ZlPmdJ')">Markdown
                                                                Syntax</a>
                                                        </small>
                                                    </v-flex>
                                                    <v-flex xs3 pl-4
                                                            style="border-left: 1px solid rgba(0, 0, 0, 0.12);">
                                                        <v-subheader class="pl-0">Preview</v-subheader>
                                                        <div v-html="materialsHtml"></div>
                                                    </v-flex>
                                                </v-layout>
                                            </v-container>
                                        </v-card>
                                    </v-dialog>
                                </v-flex>
                                <v-flex v-html="materialsHtml" v-if="materialsHtml"></v-flex>
                                <v-flex v-else>
                                    <v-layout justify-center class="py-5">
                                        <v-icon x-large>mdi-help-rhombus-outline</v-icon>
                                    </v-layout>
                                    <v-layout justify-center>
                                        <v-subheader class="text-uppercase caption">No additional materials
                                        </v-subheader>
                                    </v-layout>
                                </v-flex>
                            </v-layout>
                        </v-container>
                    </v-tab-item>
                </v-tabs-items>
            </div>
        </div>
    </div>
</template>

<script>
import common from '../../common'
import Plyr from 'plyr';
import {mapGetters} from 'vuex'
import {AXIOS} from '../../http-commons'
import showdown from 'showdown'
import {config} from '@vue/test-utils';

const converter = new showdown.Converter({
    noHeaderId: true,
    strikethrough: true,
    simpleLineBreaks: true,
    openLinksInNewWindow: true,
    underline: true
});
export default {
    name: "VideoPlayer",
    data() {
        return {
            overlay: {
                absolute: false,
                opacity: 0.46,
                overlay: false,
                zIndex: 5,
            },
            videoNavHeight: window.innerHeight - 135,
            sidebarOpen: true,
            alert: {
                active: false,
                message: '',
                color: undefined,
                btnColor: undefined,
                timeout: 0
            },
            videoOverlay: false,
            labOverlay: {
                active: false,
                courseIndex: 0,
                contentIndex: 0,
                video: {}
            },
            labOverlayTitle: '',
            labOverlayUrl: '',
            endOfCourseOverlay: false,
            endOfCourseOverlayNextCourse: '',
            endOfPathOverlay: false,
            alignTop: true,
            avatar: false,
            controlDialog: false,
            controls: [
                {button: 'Space / K', use: 'Pause'},
                {button: 'M', use: 'Mute'},
                {button: 'F', use: 'Fullscreen'},
                {button: '↑', use: 'Volume Up'},
                {button: '↓', use: 'Volume Up'},
                {button: '←', use: 'Back'},
                {button: '→', use: 'Forward'},
            ],
            currentVideo: {},
            currentCourse: {
                index: '',
                course: '',
            },
            currentPath: {
                index: '',
                courses: [],
                title: ''
            },
            dense: true,
            fillDot: false,
            formattingButtons: [],
            headerButtons: [],
            hideDot: false,
            hovered: false,
            icon: true,
            iconColor: false,
            link: {
                text: '',
                url: ''
            },
            linkDialog: false,
            left: false,
            materialsDialog: false,
            additionalMaterialAlert: false,
            note: '',
            noteDialog: false,
            otherButtons: [],
            panels: [],
            pathways: [],
            player: null,
            reverse: false,
            routeParams: {},
            rules: {
                required: value => !!value || 'Required.',
                url: value => {
                    const pattern = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/;
                    return pattern.test(value) || 'Invalid URL.'
                }
            },
            small: true,
            selectInfo: {
                startIndex: 0,
                endIndex: 0,
                lengthOfElement: 0
            },
            tabs: 2,
            tabText: ['Path', 'Additional Material'],
            tab: null,
            timestamp: false,

            selectedWord: '',
            materialsMarkdown: '',
            materialsHtml: '',
            urlToDownload: ''
        }
    },
    methods: {
        onClickOutside() {
            this.overlay.overlay = false;
        },
        getWindowHeight() {
            this.videoNavHeight = window.innerHeight - 135;
        },
        /**
         * Returns true if val is an arrow
         * Used for properly sizing arrow icons in the player controls dialog.
         * @param {string} val A string containing a single character
         * @returns {boolean}
         * @public
         */
        isArrow(val) {
            return val === '↑' || val === '↓' || val === '←' || val === '→';
        },
        /**
         * Returns true if the user is focused on the notes textarea or the notes checkbox
         * @returns {boolean}
         * @public
         */
        typing() {
            return document.activeElement.id === 'video-materials' || document.activeElement.id === 'video-notes' || document.activeElement.id === 'timestamp-checkbox'
        },
        /**
         * Saves a note to the user's account.
         * This method does not directly update the database because when the user leaves the page
         * it will already save their account to the database.
         * @param {string} formattedString String in a format similar to: 8m 42s
         * @returns {Number}
         * @public
         */
        saveNote() {
            let self = this;

            if (this.$refs.form.validate()) {
                this.$store.dispatch({
                    type: 'addNote',
                    path: this.$route.params.path,
                    course: this.$route.params.course,
                    video: this.$route.params.video,
                    note: this.note,
                    timestamp: this.timestamp ? this.player.media.currentTime : null,
                    date: Date.now()
                }).then(() => {
                    self.snackbar('success', 'Note saved successfully!', 2.5);
                }).catch((error) => {
                    console.log(error);
                    self.snackbar('error', 'An error occurred! The note may not have saved successfully.', 2.5)
                }).finally(() => {
                    self.close();
                })
            }
        },
        processClick(courseIndex, contentIndex) {
            let content = this.currentPath.courses[courseIndex].content[contentIndex];
            if (content.contentType === 'VIDEO') {
                this.toVideo(courseIndex, contentIndex)
            } else if (content.contentType === 'LAB') {
                this.urlToDownload = content.url;
                this.labOverlay = {
                    active: true,
                    courseIndex: courseIndex,
                    contentIndex: contentIndex,
                    video: content
                };
            } else if (content.contentType === 'QUIZ') {
                this.toQuiz(courseIndex, contentIndex);
            }
        },
        checkcheck() {
            this.hovered = true;
        },
        async playNextVideo() {
            this.overlay.overlay = false;

            let user = this.getUser;

            await this.getCurrentPosition();

            // some setup variables to make the logic easier to read
            let i = this.currentPath.index;
            let j = this.currentCourse.index;
            let k = this.currentVideo.index;
            let endOfCourse = (k + 1) === user.paths[i].courses[j].content.length;
            let endOfPath = (j + 1) === user.paths[i].courses.length;
            let nextItemIsVideo = false;
            let nextItemIsLabOrQuiz = false;
            if (!endOfCourse) {
                nextItemIsVideo = user.paths[i].courses[j].content[k + 1].contentType === 'VIDEO';
                nextItemIsLabOrQuiz = user.paths[i].courses[j].content[k + 1].contentType === 'LAB'
                    || user.paths[i].courses[j].content[k + 1].contentType === 'QUIZ';
            }

            // if there are no more videos or labs in course
            if (endOfCourse) {
                // if there are no more courses user is at end of path
                if (endOfPath) {
                    // end of path celebration!!
                    this.endOfPathOverlay = true;
                    console.log("END OF PATH!!")
                } else {
                    // if so, alert user or move to next course
                    this.endOfCourseOverlay = true;
                    this.endOfCourseOverlayNextCourse = j + 1;
                    console.log("END OF COURSE REACHED.")
                }

            } else {
                // check if next item in course is video
                if (nextItemIsVideo) {
                    // if so, play
                    this.videoOverlay = true;
                    setTimeout(() => {
                        this.toVideo(j, k + 1);
                        this.videoOverlay = false;
                    }, 2000);

                    // check if next index in course is lab
                } else if (nextItemIsLabOrQuiz) {
                    // if so, alert user
                    this.labOverlayTitle = user.paths[i].courses[j].content[k + 1].title;
                    this.labOverlayUrl = user.paths[i].courses[j].content[k + 1].url;
                    this.labOverlay.active = true;
                } else {
                    console.error("ERROR, NEXT ITEM IS NOT VIDEO OR LAB")
                }

            }


        },
        getCurrentPosition() {
            let user = this.getUser;
            // collapse all courses in accordian
            this.panels = []

            // iterate over the paths in the user to find the current one.
            for (let i = 0; i < user.paths.length; i++) {
                let path = this.hyphenCase(user.paths[i].title);
                // If this Path has the same title as the selected Path
                if (path === this.$route.params.path) {
                    this.currentPath.index = i;
                    for (let j = 0; j < user.paths[i].courses.length; j++) {
                        let course = this.hyphenCase(user.paths[i].courses[j].title);
                        // If this Course has the same title as the selected Course
                        if (course === this.$route.params.course) {
                            this.currentCourse.index = j;
                            this.currentCourse.course = course;
                            // expand the correct course in accordian
                            this.panels.push(this.currentCourse.index);

                            for (let k = 0; k < user.paths[i].courses[j].content.length; k++) {
                                let video = this.hyphenCase(user.paths[i].courses[j].content[k].title);
                                if (video === this.$route.params.video) {
                                    this.currentVideo.index = k;
                                }
                            }
                        }
                    }
                }
            }
        },
        /**
         * Gets the percentage completion of a course by the current user
         * @param {Object} course Course to get progress for <br>
         * @param {Number} courseIndex Index of the course in `user.paths[i].courses`
         * @returns {Number}
         * @public
         */
        getCourseProgress(course, courseIndex) {
            let progress = 0;
            let user = this.getUser;

            if (user.paths && user.paths.length) {
                for (let i = 0; i < user.paths.length; i++) {
                    if (user.paths[i].title === this.currentPath.title) {
                        for (let j = 0; j < user.paths[i].courses[courseIndex].content.length; j++) {
                            if (user.paths[i].courses[courseIndex].content[j].duration !== undefined &&
                                (user.paths[i].courses[courseIndex].content[j].url === this.currentVideo.url ||
                                    user.paths[i].courses[courseIndex].content[j].completed)) {
                                progress += user.paths[i].courses[courseIndex].content[j].duration;
                            }
                        }
                    }
                }
                return ((progress / this.totalDurationUnformatted(course)) * 100).toFixed(0);
            }
            return 0;
        },
        totalDurationUnformatted(course) {
            let duration = 0;

            if (course.hasOwnProperty('content')) {
                for (let i = 0; i < course.content.length; i++) {
                    if (!isNaN(course.content[i].duration)) {
                        duration += course.content[i].duration;
                    }
                }
            }
            return duration;
        },
        /**
         * Totals the duration of all videos in course and returns it
         * @param {Object} course <br>
         * @returns {string}
         * @public
         */
        formatTotalDuration(course) {
            let duration = 0;
            if (course.hasOwnProperty('content')) {
                for (let i = 0; i < course.content.length; i++) {
                    if (!isNaN(course.content[i].duration)) {
                        duration += course.content[i].duration;
                    }
                }
            }

            return this.formatDuration(duration);
        },
        /**
         * Converts raw seconds to appropriate larger units of time
         * @param {Number} duration Amount of time expressed only in seconds
         * @returns {string}
         * @public
         */
        formatDuration(duration) {
            let hours = Math.floor(duration / 3600);
            let minutes = Math.floor(duration / 60) % 60;
            let seconds = Math.round(duration % 60);

            return hours >= 1 ? hours + 'h ' + minutes + 'm' : minutes + 'm ' + seconds + 's';
        },
        /**
         * Routes the user back to `/pathways`.
         * This fixes an issue where the back button in-browser does not function. When on the VideoPlayer view, the last page is always
         * VideoProcessor. If the user accesses VideoProcessor, they will be routed forward and cannot go back.
         * @returns {void}
         * @public
         */
        back() {
            this.push('/pathways');
        },
        /**
         * Initializes the video player, sets up keyboard controls, and updates the player's currentTime to match user progress.
         * @returns {void}
         * @public
         */
        initPlayer() {
            this.player = new Plyr("#player", {
                controls: ['play', 'progress', 'current-time', 'mute', 'volume', 'settings', 'fullscreen']
            });

            // Plyr.io does support these out of the box, but without listening for events on window, the events will
            // only fire if the currently focused element is the video, which requires the user to click on the video,
            // and having to potentially pause and then unpause your video just so you can scrub through the video with
            // the arrow keys is bad.
            window.addEventListener('keydown', (e) => {
                if (e.code === "Space" || e.code === "KeyK") {
                    if (this.typing() === false) {
                        this.player.playing ? this.player.pause() : this.player.play();
                    }
                } else if (e.code === "ArrowLeft") {
                    if (this.typing() === false) {
                        e.preventDefault();
                        e.stopPropagation();
                        this.player.rewind();
                    }
                } else if (e.code === "ArrowRight") {
                    if (this.typing() === false) {
                        e.preventDefault();
                        e.stopPropagation();
                        this.player.forward();
                    }
                } else if (e.code === "ArrowUp") {
                    if (this.typing() === false) {
                        e.preventDefault();
                        e.stopPropagation();
                        this.player.increaseVolume(0.05);
                    }
                } else if (e.code === "ArrowDown") {
                    if (this.typing() === false) {
                        e.preventDefault();
                        e.stopPropagation();
                        this.player.decreaseVolume(0.05);
                    }
                } else if (e.code === "KeyM") {
                    if (this.typing() === false) {
                        this.player.muted = !this.player.muted;
                    }
                } else if (e.code === "KeyF") {
                    if (this.typing() === false) {
                        this.player.fullscreen.toggle();
                    }
                }

            });

            this.player.seekTime = 5;

            this.player.source = {
                type: 'video',
                // title: 'video-title',
                sources: [
                    {
                        src: this.currentVideo.url,
                        type: 'video/mp4',
                        size: 576
                    },
                ],
            };

            this.player.media.currentTime = this.currentVideo.currentTime ? this.currentVideo.currentTime : 0.001;
        },
        /**
         * Initializes the page by setting `this.currentPath`, updating tab names, and processing the timestamp query if it exists
         * @returns {string}
         * @public
         */
        initPage() {
            let self = this;
            AXIOS.get('/api/paths?' + this.$route.params.path)
                .then((response) => {
                    if (response.data.length > 1) {
                        response.data.forEach(d => {
                            const title = d.title.toLowerCase().replaceAll(" ", "-");
                            if (title === this.$route.params.path) self.currentPath = d;
                        })
                    } else self.currentPath = response.data[0];

                    self.tabText[0] = self.currentPath.title;

                    self.routeParams = self.$route.params;

                })
                .catch((error) => {
                    console.log(error);
                });
        },
        /**
         * Returns the appropriate color for the icon's parent element
         * @param {string} icon Name of the icon
         * @returns {string}
         * @public
         */
        getColor(icon) {
            switch (icon) {
                case 'mdi-play':
                    return 'primary';
                case 'mdi-check-bold':
                    return 'success';
                case undefined:
                    return 'grey';
            }
        },
        /**
         * Returns the appropriate icon for the video based on if it has been completed or is the current video
         * @param {Number} courseIndex Index of the course in `this.currentPath`<br>
         * @param {Number} videoIndex Index of the video in `this.currentPath.courses[courseIndex].content`
         * @returns {string}
         * @public
         */
        getIcon(courseIndex, videoIndex) {
            let user = this.getUser;

            if (user.paths && user.paths.length) {
                for (let i = 0; i < user.paths.length; i++) {
                    if (user.paths[i].title === this.currentPath.title) {
                        let content = user.paths[i].courses[courseIndex].content[videoIndex];
                        if (content.url === this.currentVideo.url) {
                            return 'mdi-play'
                        } else if (content.completed) {
                            return 'mdi-check-bold'
                        } else {
                            return undefined
                        }
                    }
                }
            }
            return 'mdi-alert-circle-outline'
        },
        hasAdditionalMaterials() {
            return this.currentVideo.materialsMarkdown ? true : false;
        },
        /**
         * Sets the last video completed to the video that the user clicked on (necessary for how VideoProcessor works)
         * and then routes to the VideoProcessor
         * @param {Number} courseIndex Index of the course in `this.currentPath`<br>
         * @param {Number} videoIndex Index of the video in `this.currentPath.courses[courseIndex].content`
         * @returns {void}
         * @public
         */
        toVideo(courseIndex, videoIndex) {
            let self = this;
            this.$store.dispatch({
                type: 'setLastVideo',
                lastVideo: this.currentPath.courses[courseIndex].content[videoIndex],
                videoIndex: videoIndex,
                courseIndex: courseIndex,
                path: this.currentPath,
            }).then(() => {
                self.push('/player/pathway/' + self.$route.params.path + '/course/' + self.hyphenCase(self.currentPath.courses[courseIndex].title) + '/video')
            }).catch((error) => {
                console.log(error);
            })
        },
        toQuiz(courseIndex, videoIndex) {
            this.$store.state.quizRedirectUrl = this.$route.name;
            let path = this.$route.path;
            this.$store.dispatch({
                type: 'setLastVideo',
                lastVideo: this.currentPath.courses[courseIndex].content[videoIndex],
                videoIndex: videoIndex,
                courseIndex: courseIndex,
                path: this.currentPath
            });
            this.$store.dispatch({
                type: 'setQuizRedirectUrl',
                url: '/player/pathway/' + this.$route.params.path + '/course/' + this.hyphenCase(this.currentPath.courses[courseIndex].title) + '/video',
                path: 'player'
            }).then(() => {
                this.push('/quizzes/' + this.currentPath.courses[courseIndex].content[videoIndex].id)
            }).catch((error) => {
                console.log(error);
            })
        },
        /**
         * Updates the video player's `currentTime` property to match the time in the timestamp query, if it exists.
         * @returns {void}
         * @public
         */
        updateTimestamp() {
            if (this.$route.query.hasOwnProperty("timestamp")) {
                this.player.media.currentTime = this.$route.query.timestamp;
            }
        },
        /**
         * Closes the note dialog, resets fields and field validation
         * @returns {void}
         * @public
         */
        close() {
            this.noteDialog = false;
            if (this.$refs.form) {
                this.$refs.form.reset();
            }
        },
        /**
         * Returns the currently selected text
         * @returns {String}
         * @public
         */
        getSelectedWord() {
            let self = this;
            if (!!document.getSelection) {
                let selectedElement = document.getSelection().focusNode.lastChild;
                if (selectedElement && selectedElement.value) {
                    setTimeout(function () {
                        if (selectedElement.selectionStart === selectedElement.selectionEnd) {
                            self.selectedWord = '';
                            self.selectInfo.startIndex = selectedElement.selectionStart;
                            self.selectInfo.endIndex = selectedElement.selectionEnd;
                            self.selectInfo.lengthOfElement = selectedElement.value.length;
                        } else {
                            self.selectedWord = selectedElement.value.slice(selectedElement.selectionStart, selectedElement.selectionEnd);
                            self.selectInfo.startIndex = selectedElement.selectionStart;
                            self.selectInfo.endIndex = selectedElement.selectionEnd;
                            self.selectInfo.lengthOfElement = selectedElement.value.length;
                        }
                    }, 100)
                }
            } else if (!!window.getSelection) {
                let selectedElement = document.getSelection().focusNode.lastChild;
                if (selectedElement) {
                    setTimeout(function () {
                        if (selectedElement.selectionStart === selectedElement.selectionEnd) {
                            self.selectedWord = '';
                            self.selectInfo.startIndex = selectedElement.selectionStart;
                            self.selectInfo.endIndex = selectedElement.selectionEnd;
                            self.selectInfo.lengthOfElement = selectedElement.value.length;
                        } else {
                            self.selectedWord = selectedElement.value.slice(selectedElement.selectionStart, selectedElement.selectionEnd);
                            self.selectInfo.startIndex = selectedElement.selectionStart;
                            self.selectInfo.endIndex = selectedElement.selectionEnd;
                            self.selectInfo.lengthOfElement = selectedElement.value.length;
                        }
                    }, 100)
                }
            } else {
                this.selectedWord = '';
                self.selectInfo.startIndex = 0;
                self.selectInfo.endIndex = 0;
                self.selectInfo.lengthOfElement = 0;
            }
        },
        /**
         * Converts the contents of text from plain text or markdown to HTML
         * @returns {void}
         * @public
         */
        markdownToHtml(text) {
            this.materialsHtml = converter.makeHtml(text);
        },
        /**
         * Gets the line number where the carat is currently positioned
         * @returns {Number}
         * @void
         */
        getTextAreaLineNumber() {
            let textarea = document.getElementById("video-materials");
            return textarea.value.substr(0, textarea.selectionStart).split("\n").length - 1;
        },
        /**
         * Returns the contents of the textarea in the Additional Materials editor
         * @returns {Array}
         * @public
         */
        getAllTextAreaLines() {
            let textarea = document.getElementById("video-materials");
            return textarea.value.split("\n");
        },
        /**
         * Counts the consecutive occurrences of a character at the beginning of a string
         * @param {String} string The string to iterate through
         * @param {String} character The character to search for in string
         * @returns {Number}
         * @public
         */
        countOccurrences(string, character) {
            let count = 0;
            for (let i = 0; i < string.length; i++) {
                if (string.charAt(i) === character) {
                    count++;
                } else {
                    return count;
                }
            }
            return count;
        },
        /**
         * Adds the contents of `this.link` to `this.materialsMarkdown` in the correct format
         * @returns {void}
         * @public
         */
        addLink() {
            let link = Object.assign({}, this.link);
            this.linkDialog = false;
            this.$refs.linkForm.reset();

            let allLines = this.getAllTextAreaLines();

            let selectedWordStart = this.selectInfo.lengthOfElement - (this.selectInfo.lengthOfElement - this.selectInfo.startIndex);
            let selectedWordEnd = this.selectInfo.lengthOfElement - (this.selectInfo.lengthOfElement - this.selectInfo.endIndex);

            allLines = allLines.join("\n");

            this.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '[' + link.text + '](' + link.url + ')' + allLines.toString().slice(selectedWordEnd);

        },
        /**
         * Closes `linkDialog` and resets all fields and field validation.
         * @returns {void}
         * @public
         */
        closeLinkDialog() {
            this.linkDialog = false;
            this.$refs.linkForm.reset();
        },
        /**
         * Initializes the contents of buttons in the Additional Materials editor
         * @returns {void}
         * @public
         */
        initButtons() {
            let self = this;
            let headerButtons = [
                {
                    icon: 'mdi-format-header-1',
                    tooltip: 'Header 1',
                    operation() {
                        let lineNumber = self.getTextAreaLineNumber();
                        let allLines = self.getAllTextAreaLines();

                        if (allLines[lineNumber].startsWith("#")) {
                            let headerCount = self.countOccurrences(allLines[lineNumber], "#"); // count number of #'s
                            allLines[lineNumber] = allLines[lineNumber].substring(headerCount); // remove #
                            allLines[lineNumber] = allLines[lineNumber].trim(); // remove any leading/trailing whitespace

                            if (headerCount !== 1) {
                                allLines[lineNumber] = "# " + allLines[lineNumber];
                            }
                        } else {
                            let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                            let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                            if (selectedWordStart === selectedWordEnd) {
                                allLines[lineNumber] = "# " + allLines[lineNumber];
                            } else if (selectedWordStart !== selectedWordEnd) {
                                allLines = allLines.join("\n");
                                if (allLines.toString().slice(selectedWordStart, selectedWordEnd).endsWith(" ")) {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n# ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd);
                                } else {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n# ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd + 1);
                                }
                                return;
                            }
                        }
                        self.materialsMarkdown = allLines.join("\n");
                    }
                },
                {
                    icon: 'mdi-format-header-2',
                    tooltip: 'Header 2',
                    operation() {
                        let lineNumber = self.getTextAreaLineNumber();
                        let allLines = self.getAllTextAreaLines();

                        if (allLines[lineNumber].startsWith("#")) {
                            let headerCount = self.countOccurrences(allLines[lineNumber], "#"); // count number of #'s
                            allLines[lineNumber] = allLines[lineNumber].substring(headerCount); // remove #
                            allLines[lineNumber] = allLines[lineNumber].trim(); // remove any leading/trailing whitespace

                            if (headerCount !== 2) {
                                allLines[lineNumber] = "## " + allLines[lineNumber];
                            }
                        } else {
                            let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                            let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                            if (selectedWordStart === selectedWordEnd) {
                                allLines[lineNumber] = "## " + allLines[lineNumber];
                            } else if (selectedWordStart !== selectedWordEnd) {
                                allLines = allLines.join("\n");
                                if (allLines.toString().slice(selectedWordStart, selectedWordEnd).endsWith(" ")) {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n## ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd);
                                } else {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n## ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd + 1);
                                }
                                return;
                            }
                        }
                        self.materialsMarkdown = allLines.join("\n");
                    }
                },
                {
                    icon: 'mdi-format-header-3',
                    tooltip: 'Header 3',
                    operation() {
                        let lineNumber = self.getTextAreaLineNumber();
                        let allLines = self.getAllTextAreaLines();

                        if (allLines[lineNumber].startsWith("#")) {
                            let headerCount = self.countOccurrences(allLines[lineNumber], "#"); // count number of #'s
                            allLines[lineNumber] = allLines[lineNumber].substring(headerCount); // remove #
                            allLines[lineNumber] = allLines[lineNumber].trim(); // remove any leading/trailing whitespace

                            if (headerCount !== 3) {
                                allLines[lineNumber] = "### " + allLines[lineNumber];
                            }
                        } else {
                            let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                            let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                            if (selectedWordStart === selectedWordEnd) {
                                allLines[lineNumber] = "### " + allLines[lineNumber];
                            } else if (selectedWordStart !== selectedWordEnd) {
                                allLines = allLines.join("\n");
                                if (allLines.toString().slice(selectedWordStart, selectedWordEnd).endsWith(" ")) {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n### ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd);
                                } else {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n### ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd + 1);
                                }
                                return;
                            }
                        }
                        self.materialsMarkdown = allLines.join("\n");
                    }
                },
                {
                    icon: 'mdi-format-header-4',
                    tooltip: 'Header 4',
                    operation() {
                        let lineNumber = self.getTextAreaLineNumber();
                        let allLines = self.getAllTextAreaLines();

                        if (allLines[lineNumber].startsWith("#")) {
                            let headerCount = self.countOccurrences(allLines[lineNumber], "#"); // count number of #'s
                            allLines[lineNumber] = allLines[lineNumber].substring(headerCount); // remove #
                            allLines[lineNumber] = allLines[lineNumber].trim() // remove any leading/trailing whitespace

                            if (headerCount !== 4) {
                                allLines[lineNumber] = "#### " + allLines[lineNumber];
                            }
                        } else {
                            let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                            let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                            if (selectedWordStart === selectedWordEnd) {
                                allLines[lineNumber] = "#### " + allLines[lineNumber];
                            } else if (selectedWordStart !== selectedWordEnd) {
                                allLines = allLines.join("\n");
                                if (allLines.toString().slice(selectedWordStart, selectedWordEnd).endsWith(" ")) {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n#### ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd);
                                } else {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n#### ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd + 1);
                                }
                                return;
                            }
                        }
                        self.materialsMarkdown = allLines.join("\n");
                    }
                },
                {
                    icon: 'mdi-format-header-5',
                    tooltip: 'Header 5',
                    operation() {
                        let lineNumber = self.getTextAreaLineNumber();
                        let allLines = self.getAllTextAreaLines();

                        if (allLines[lineNumber].startsWith("#")) {
                            let headerCount = self.countOccurrences(allLines[lineNumber], "#"); // count number of #'s
                            allLines[lineNumber] = allLines[lineNumber].substring(headerCount); // remove #
                            allLines[lineNumber] = allLines[lineNumber].trim(); // remove any leading/trailing whitespace

                            if (headerCount !== 5) {
                                allLines[lineNumber] = "##### " + allLines[lineNumber];
                            }
                        } else {
                            let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                            let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                            if (selectedWordStart === selectedWordEnd) {
                                allLines[lineNumber] = "##### " + allLines[lineNumber];
                            } else if (selectedWordStart !== selectedWordEnd) {
                                allLines = allLines.join("\n");
                                if (allLines.toString().slice(selectedWordStart, selectedWordEnd).endsWith(" ")) {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n##### ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd);
                                } else {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n##### ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd + 1);
                                }
                                return;
                            }
                        }
                        self.materialsMarkdown = allLines.join("\n");
                    }
                },
                {
                    icon: 'mdi-format-header-6',
                    tooltip: 'Header 6',
                    operation() {
                        let lineNumber = self.getTextAreaLineNumber();
                        let allLines = self.getAllTextAreaLines();

                        if (allLines[lineNumber].startsWith("#")) {
                            let headerCount = self.countOccurrences(allLines[lineNumber], "#"); // count number of #'s
                            allLines[lineNumber] = allLines[lineNumber].substring(headerCount); // remove #
                            allLines[lineNumber] = allLines[lineNumber].trim(); // remove any leading/trailing whitespace

                            if (headerCount !== 6) {
                                allLines[lineNumber] = "###### " + allLines[lineNumber];
                            }
                        } else {
                            let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                            let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                            if (selectedWordStart === selectedWordEnd) {
                                allLines[lineNumber] = "###### " + allLines[lineNumber];
                            } else if (selectedWordStart !== selectedWordEnd) {
                                allLines = allLines.join("\n");
                                if (allLines.toString().slice(selectedWordStart, selectedWordEnd).endsWith(" ")) {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n###### ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd);
                                } else {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n###### ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd + 1);
                                }
                                return;
                            }
                        }
                        self.materialsMarkdown = allLines.join("\n");
                    }
                }
            ];
            let formattingButtons = [
                {
                    icon: 'mdi-format-bold',
                    tooltip: 'Bold',
                    operation() {
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        allLines = allLines.join("\n");

                        if (selectedWordStart !== selectedWordEnd) {
                            // Remove adjacent characters from both sides of selectedWord
                            if (allLines.slice(selectedWordStart - 2, selectedWordEnd + 2).startsWith('**') && allLines.slice(selectedWordStart - 2, selectedWordEnd + 2).endsWith('**')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart - 2) + allLines.toString().slice(selectedWordStart, selectedWordEnd) + allLines.toString().slice(selectedWordEnd + 2);
                            }
                            // Remove 2 characters from both sides of selectedWord
                            else if (allLines.slice(selectedWordStart, selectedWordEnd).startsWith('**') && allLines.slice(selectedWordStart, selectedWordEnd).endsWith('**')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + allLines.toString().slice(selectedWordStart + 2, selectedWordEnd - 2) + allLines.toString().slice(selectedWordEnd);
                            }
                            // Add characters to both sides of selectedWord
                            else self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '**' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '**' + allLines.toString().slice(selectedWordEnd);
                        }

                        self.selectedWord = '';
                        self.selectInfo.startIndex = '';
                        self.selectInfo.endIndex = '';
                        self.selectInfo.lengthOfElement = '';
                    }
                },
                {
                    icon: 'mdi-format-italic',
                    tooltip: 'Italics',
                    operation() {
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        allLines = allLines.join("\n");

                        if (selectedWordStart !== selectedWordEnd) {
                            // Remove adjacent characters from both sides of selectedWord (selectedWord does not contain *)
                            if (allLines.slice(selectedWordStart - 1, selectedWordEnd + 1).startsWith('*') && allLines.slice(selectedWordStart - 1, selectedWordEnd + 1).endsWith('*') &&
                                !allLines.slice(selectedWordStart - 2, selectedWordEnd + 2).startsWith('**') && !allLines.slice(selectedWordStart - 2, selectedWordEnd + 2).endsWith('**') ||
                                allLines.slice(selectedWordStart - 3, selectedWordEnd + 3).startsWith('***') && allLines.slice(selectedWordStart - 3, selectedWordEnd + 3).endsWith('***')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart - 1) + allLines.toString().slice(selectedWordStart, selectedWordEnd) + allLines.toString().slice(selectedWordEnd + 1);
                            }
                            // Remove characters from both sides of selectedWord (selectedWord contains *)
                            else if (allLines.slice(selectedWordStart, selectedWordEnd).startsWith('*') && allLines.slice(selectedWordStart, selectedWordEnd).endsWith('*') &&
                                !allLines.slice(selectedWordStart, selectedWordEnd).startsWith('**') && !allLines.slice(selectedWordStart, selectedWordEnd).endsWith('**') &&
                                allLines.slice(selectedWordStart, selectedWordEnd).startsWith('***') && allLines.slice(selectedWordStart, selectedWordEnd).endsWith('***') ||
                                allLines.slice(selectedWordStart, selectedWordEnd).startsWith('*') && allLines.slice(selectedWordStart, selectedWordEnd).endsWith('*') &&
                                !allLines.slice(selectedWordStart, selectedWordEnd).startsWith('**') && !allLines.slice(selectedWordStart, selectedWordEnd).endsWith('**') &&
                                !allLines.slice(selectedWordStart, selectedWordEnd).startsWith('***') && !allLines.slice(selectedWordStart, selectedWordEnd).endsWith('***')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + allLines.toString().slice(selectedWordStart + 1, selectedWordEnd - 1) + allLines.toString().slice(selectedWordEnd);
                            }
                            // Add character to both sides of selectedWord
                            else self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '*' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '*' + allLines.toString().slice(selectedWordEnd);
                        }

                        self.selectedWord = '';
                        self.selectInfo.startIndex = '';
                        self.selectInfo.endIndex = '';
                        self.selectInfo.lengthOfElement = '';

                    }
                },
                {
                    icon: 'mdi-format-underline',
                    tooltip: 'Underline',
                    operation() {
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        allLines = allLines.join("\n");

                        if (selectedWordStart !== selectedWordEnd) {
                            // Remove adjacent characters from both sides of selectedWord
                            if (allLines.slice(selectedWordStart - 3, selectedWordEnd + 3).startsWith('___') && allLines.slice(selectedWordStart - 3, selectedWordEnd + 3).endsWith('___')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart - 3) + allLines.toString().slice(selectedWordStart, selectedWordEnd) + allLines.toString().slice(selectedWordEnd + 3);
                            }
                            // Remove 3 characters from both sides of selectedWord
                            else if (allLines.slice(selectedWordStart, selectedWordEnd).startsWith('___') && allLines.slice(selectedWordStart, selectedWordEnd).endsWith('___')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + allLines.toString().slice(selectedWordStart + 3, selectedWordEnd - 3) + allLines.toString().slice(selectedWordEnd);
                            }
                            // Add characters to both sides of selectedWord
                            else self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '___' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '___' + allLines.toString().slice(selectedWordEnd);
                        }

                        self.selectedWord = '';
                        self.selectInfo.startIndex = '';
                        self.selectInfo.endIndex = '';
                        self.selectInfo.lengthOfElement = '';

                    }
                },
                {
                    icon: 'mdi-format-strikethrough',
                    tooltip: 'Strikethrough',
                    operation() {
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        allLines = allLines.join("\n");

                        if (selectedWordStart !== selectedWordEnd) {
                            // Remove adjacent characters from both sides of selectedWord
                            if (allLines.slice(selectedWordStart - 2, selectedWordEnd + 2).startsWith('~~') && allLines.slice(selectedWordStart - 2, selectedWordEnd + 2).endsWith('~~')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart - 2) + allLines.toString().slice(selectedWordStart, selectedWordEnd) + allLines.toString().slice(selectedWordEnd + 2);
                            }
                            // Remove 2 characters from both sides of selectedWord
                            else if (allLines.slice(selectedWordStart, selectedWordEnd).startsWith('~~') && allLines.slice(selectedWordStart, selectedWordEnd).endsWith('~~')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + allLines.toString().slice(selectedWordStart + 2, selectedWordEnd - 2) + allLines.toString().slice(selectedWordEnd);
                            }
                            // Add characters to both sides of selectedWord
                            else self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '~~' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '~~' + allLines.toString().slice(selectedWordEnd);
                        }

                        self.selectedWord = '';
                        self.selectInfo.startIndex = '';
                        self.selectInfo.endIndex = '';
                        self.selectInfo.lengthOfElement = '';

                    }
                }
            ];
            let otherButtons = [
                {
                    icon: 'mdi-camera-plus-outline',
                    tooltip: 'Add Image',
                    operation() {
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        allLines = allLines.join("\n");

                        if (selectedWordStart !== selectedWordEnd) {
                            let selectedWord = allLines.toString().slice(selectedWordStart, selectedWordEnd);

                            self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n![' + selectedWord + ']( LINK-TO-IMAGE =250x250)\n' + allLines.toString().slice(selectedWordEnd);
                        } else {
                            if (allLines.toString() === "") {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '![]( LINK-TO-IMAGE =250x250)\n' + allLines.toString().slice(selectedWordEnd);
                            } else {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n![]( LINK-TO-IMAGE =250x250)\n' + allLines.toString().slice(selectedWordEnd);

                            }
                        }
                    }
                },
                {
                    icon: 'mdi-link-plus',
                    tooltip: 'Add Hyperlink',
                    operation() {
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        allLines = allLines.join("\n");

                        if (selectedWordStart !== selectedWordEnd) {
                            self.link.text = allLines.toString().slice(selectedWordStart, selectedWordEnd);
                        }

                        self.linkDialog = true;
                    }
                },
                {
                    icon: 'mdi-code-braces',
                    tooltip: 'Code Block',
                    operation() {
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        allLines = allLines.join("\n");

                        if (selectedWordStart !== selectedWordEnd) {
                            // Remove adjacent characters from both sides of selectedWord
                            if (allLines.slice(selectedWordStart - 3, selectedWordEnd + 3).startsWith('```') && allLines.slice(selectedWordStart - 3, selectedWordEnd + 3).endsWith('```')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart - 3) + allLines.toString().slice(selectedWordStart, selectedWordEnd) + allLines.toString().slice(selectedWordEnd + 3);
                            }
                            // Remove 3 characters from both sides of selectedWord
                            else if (allLines.slice(selectedWordStart, selectedWordEnd).startsWith('```') && allLines.slice(selectedWordStart, selectedWordEnd).endsWith('```')) {
                                self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + allLines.toString().slice(selectedWordStart + 3, selectedWordEnd - 3) + allLines.toString().slice(selectedWordEnd);
                            }
                            // Add characters to both sides of selectedWord
                            else self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '```' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '```' + allLines.toString().slice(selectedWordEnd);
                        }

                    }
                },
                {
                    icon: 'mdi-format-list-bulleted',
                    tooltip: 'Bulleted List',
                    operation() {
                        let lineNumber = self.getTextAreaLineNumber();
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        if (allLines[lineNumber].startsWith("-")) {
                            let headerCount = self.countOccurrences(allLines[lineNumber], "-"); // count number of -'s
                            allLines[lineNumber] = allLines[lineNumber].substring(headerCount); // remove -
                            allLines[lineNumber] = allLines[lineNumber].trim(); // remove any leading/trailing whitespace

                            if (headerCount !== 1) {
                                allLines[lineNumber] = "- " + allLines[lineNumber];
                            }
                        } else {
                            if (selectedWordStart === selectedWordEnd) {
                                allLines[lineNumber] = "- " + allLines[lineNumber];
                            } else if (selectedWordStart !== selectedWordEnd) {
                                allLines = allLines.join("\n");
                                if (allLines.toString().slice(selectedWordStart, selectedWordEnd).endsWith(" ")) {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n- ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd);
                                } else {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n- ' + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd + 1);
                                }
                                return;
                            }
                        }
                        self.materialsMarkdown = allLines.join("\n");
                    }
                },
                {
                    icon: 'mdi-format-list-numbered',
                    tooltip: 'Numbered List',
                    operation() {
                        let lineNumber = self.getTextAreaLineNumber();
                        let allLines = self.getAllTextAreaLines();

                        let selectedWordStart = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.startIndex);
                        let selectedWordEnd = self.selectInfo.lengthOfElement - (self.selectInfo.lengthOfElement - self.selectInfo.endIndex);

                        let listNumber = allLines.join("\n").match(/[1-9][0-9]{0,2}\.{1}/gm) ? allLines.join("\n").match(/[1-9][0-9]{0,2}\.{1}/gm).length + 1 : 1;

                        if (allLines[lineNumber].match(/^\d/)) {
                            allLines[lineNumber] = allLines[lineNumber].slice(allLines[lineNumber].indexOf(".") + 1); // remove number
                            allLines[lineNumber] = allLines[lineNumber].trim(); // remove any leading/trailing whitespace
                        } else {
                            if (selectedWordStart === selectedWordEnd) {
                                allLines[lineNumber] = listNumber + ". " + allLines[lineNumber];
                            } else if (selectedWordStart !== selectedWordEnd) {
                                allLines = allLines.join("\n");
                                if (allLines.toString().slice(selectedWordStart, selectedWordEnd).endsWith(" ")) {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n' + listNumber + ". " + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd);
                                } else {
                                    self.materialsMarkdown = allLines.toString().slice(0, selectedWordStart) + '\n' + listNumber + ". " + allLines.toString().slice(selectedWordStart, selectedWordEnd) + '\n\n' + allLines.toString().slice(selectedWordEnd + 1);
                                }
                                return;
                            }
                        }
                        self.materialsMarkdown = allLines.join("\n");
                    }
                }
            ];

            // H1 ... H6
            this.headerButtons = headerButtons;
            // Bold ... Strikethrough
            this.formattingButtons = formattingButtons;
            // Add Image ... Numbered List
            this.otherButtons = otherButtons;
        },
        /**
         * Opens URL in a new browser tab
         * @returns {void}
         * @public
         */
        openNewTab() {
            let newTab = window.open(this.urlToDownload, '_blank');
            newTab.focus();
            const isVideo = false;
            this.setCurrentItemAsCompleted(isVideo);
            this.labOverlay.active = false
            this.processClick(this.labOverlay.courseIndex, this.labOverlay.contentIndex + 1);
        },
        /**
         * Saves the contents of `this.materialsMarkdown` to the database
         * @returns {void}
         * @public
         */
        saveAdditionalMaterial() {
            this.materialsDialog = false;

            AXIOS.put('/api/courses/additionalMaterials', {
                title: this.currentPath.courses[this.currentCourse.index].title,
                index: this.currentVideo.index,
                markdown: this.materialsMarkdown
            })
                .then(() => {
                    AXIOS.get('/api/users')
                        .then((response) => {
                            let users = response.data;

                            for (let k = 0; k < users.length; k++) {
                                if (users[k].paths) {
                                    for (let l = 0; l < users[k].paths.length; l++) {
                                        if (users[k].paths[l].id === this.currentPath.id) {
                                            users[k].paths[l].courses[this.currentCourse.index].content[this.currentVideo.index].materialsMarkdown = this.materialsMarkdown;
                                            AXIOS.post('/api/users', users[k])
                                                .then((response) => {
                                                    if (response.data.username === this.getUser.username) {
                                                        this.$store.dispatch({
                                                            type: 'setUser',
                                                            user: response.data
                                                        });
                                                        this.$store.dispatch({
                                                            type: 'setCurrentVideoAdditionalMaterials',
                                                            additionalMaterials: this.materialsMarkdown
                                                        })
                                                    }
                                                    this.snackbar('success', 'Successfully updated video materials!', 2.5);
                                                })
                                                .catch((error) => {
                                                    console.log(error);
                                                    this.snackbar('error', 'An error occurred while updating users!', 2.5);
                                                })
                                        }
                                    }
                                }
                            }
                        })
                        .catch((error) => {
                            console.log(error);
                            this.snackbar('error', 'An error occurred while getting users!', 2.5);
                        })
                })
                .catch((error) => {
                    console.log(error);
                    this.snackbar('error', 'An error occurred while saving to the database!', 2.5);
                })
        },
        setCurrentItemAsCompleted(isVideo) {
            // Set video as completed before leaving the page
            let user = this.getUser;
            let self = this;
            if (user.paths && user.paths.length) {
                outer:
                    for (let i = 0; i < user.paths.length; i++) {
                        if (user.paths[i].title === this.currentPath.title || !isVideo) {
                            for (let j = 0; j < user.paths[i].courses.length; j++) {
                                if (this.hyphenCase(user.paths[i].courses[j].title) === this.routeParams.course) {
                                    for (let k = 0; k < user.paths[i].courses[j].content.length; k++) {
                                        if (this.hyphenCase(user.paths[i].courses[j].content[k].title) === this.hyphenCase(this.routeParams.video)
                                            || this.hyphenCase(user.paths[i].courses[j].content[k].title) === this.labOverlay.video.title) {
                                            let dispatchType = '';
                                            if (self.player.media && (Math.floor(self.player.media.duration) === Math.floor(self.player.media.currentTime)
                                                || isNaN(self.player.media.duration) || !isVideo)
                                            ) {
                                                dispatchType = 'setVideoCompleted';
                                            } else {
                                                dispatchType = 'setWatchingVideo';
                                            }

                                            if (!isVideo) {
                                                j = this.labOverlay.courseIndex;
                                                k = this.labOverlay.contentIndex;
                                            }

                                            this.$store.dispatch({
                                                type: dispatchType,
                                                pathIndex: i,
                                                courseIndex: j,
                                                videoIndex: k,
                                                currentTime: self.player.media ? self.player.media.currentTime : 0
                                            }).then(() => {
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
                                            }).catch((error) => {
                                                console.log(error);
                                            })
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    }
            } else {
                this.snackbar('error', 'An error occurred! Could not save progress!', 2.5);
                this.$store.dispatch({
                    type: 'setProcessing',
                    processing: false
                }).catch((error) => {
                    console.info("There is a problem with the current user structure. Cannot save to the database.");
                    console.log(error);
                })
            }
        }


    },
    watch: {
        materialsMarkdown: function () {
            this.markdownToHtml(this.materialsMarkdown);
        }
    },
    beforeDestroy() {
        this.setCurrentItemAsCompleted(this.currentVideo.contentType === 'VIDEO');
    },
    mounted() {
        this.snackbar = common.snackbar;
        this.push = common.push;
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
        })

        this.initPage();

        this.currentVideo = this.$store.getters.getCurrentVideo;

        if (this.currentVideo === null) {
            // The application did not go through the Video Processor before reaching the page. Redirect.
            this.$router.push(this.$route.fullPath.substring(0, (this.$route.fullPath.lastIndexOf('/'))));
        }

        this.initPlayer();
        this.updateTimestamp();
        this.initButtons();

        this.materialsMarkdown = this.currentVideo.materialsMarkdown;

        this.getCurrentPosition();

        this.$store.dispatch({
            type: 'setProcessing',
            processing: true
        });

        this.additionalMaterialAlert = this.hasAdditionalMaterials();

        this.player.on('ended', event => {
            this.playNextVideo();
        })

        this.overlay.overlay = this.currentVideo.contentType === 'QUIZ'
        if (this.overlay.overlay) {
            this.labOverlay = {
                active: false,
                courseIndex: this.currentCourse.index,
                contentIndex: this.currentVideo.index,
                video: this.currentVideo
            };
        }

    },

    computed: {
        showSidebar() {
            return this.sidebarOpen ? 'inline' : 'none';
        },
        isAdmin() {
            return this.getRole === 'ADMIN'
        },
        ...mapGetters([
            'getUser',
            'getCurrentVideo',
            'getUserPaths',
            'getRole'
        ]),
        /**
         * Returns the state of `this.$vuetify.theme.dark`
         * @returns {boolean}
         * @public
         */
        darkMode() {
            return this.$vuetify.theme.dark;
        },
        /**
         * Iterates through `this.pathways` to find the path that corresponds with the title of the path in the URL
         * @returns {Object}
         * @public
         */
        getPath() {
            for (let i = 0; i < this.pathways.length; i++) {
                if (this.hyphenCase(this.pathways[i].title) === this.hyphenCase(this.$route.params.path)) {
                    return this.pathways[i];
                }
            }
        }
    },

    created() {
        window.addEventListener("windowResize", this.getWindowHeight);
    },

    destroyed() {
        window.removeEventListener("windowResize", this.getWindowHeight);
    }
}
</script>

<style scoped>
@media all and (max-width: 989px) and (min-width: 0px) {
    .page-layout {
        display: grid;
        grid-template-rows: auto auto;
    }

    .menu-bar {
        display: none;
    }
}

@media all and (min-width: 990px) {
    .video-bar {
        width: 360px;
    }

    .page-layout {
        display: grid;
        grid-template-columns: 1fr auto;
    }
}

.video-player-section {
    width: 100%;
    align-self: center;
}

.right-border {
    border-right: 1px solid rgba(0, 0, 0, 0.12);
}

.button-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
}

.menu-button-placement {
    display: grid;
    justify-items: end;
}

.video-nav-bar-header {
    width: 100%;
    display: grid;
    grid-template-rows: 1fr 35px
}

.current-video-title {
    margin: 20px 20px 5px 20px;
}

.nav-tools {
    margin-right: 15px;
}

.course-expansion-panel {
    display: grid;
    grid-template-columns: 1fr 12fr 1fr
}

.page-layout::-webkit-scrollbar {
    display: none;
}

</style>

<docs>
Main hub for watching videos and working through a course. Provides an avenue for creating notes and associating
them to a video.

---
</docs>