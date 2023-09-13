<template>
    <v-app class="grey lighten-4">
        <v-container fill-height>
            <v-layout fill-height align-center justify-center>
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
                        @click="push('/pathways')"
                        class="caption"
                    >
                        Return
                    </v-btn>
                </v-snackbar>
                <v-progress-circular indeterminate color="primary" :size="size" :width="width"></v-progress-circular>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
import common from '../../common'
import {mapActions, mapGetters} from 'vuex'
import {AXIOS} from "@/http-commons";

export default {
    name: "VideoProcessor",
    data() {
        return {
            size: 75,
            alert: {
                active: false,
                message: '',
                color: undefined,
                btnColor: undefined,
                timeout: 0
            },
        }
    },
    mounted() {
        let self = this;
        this.snackbar = common.snackbar;
        this.push = common.push;
        this.hyphenCase = common.hyphenCase;
        this.getVideo();

        this.$on('openAlert', (msg) => {
            this.alert = {
                active: true,
                color: msg.color,
                message: msg.alertMessage,
                timeout: 0
            };
        });

        setTimeout(function () {
            self.snackbar('warning', "Oops! We can't find the video you're looking for...", 0)
        }, 3500)
    },
    methods: {
        /**
         * Checks for timestamp in the router query and returns the timestamp query if true.
         * Allows timestamp functionality to work.
         * @returns {string}
         * @public
         */
        getTimestamp() {
            if (this.$route.query.hasOwnProperty("timestamp")) {
                return '?timestamp=' + this.$route.query.timestamp;
            }
            return '';
        },
        /**
         * Iterates through a user's videos to find the video with a matching title of `this.$store.currentVideo.title`
         * Then routes the user to the video player with that video URL.
         * @returns {void}
         * @public
         */
        getVideo() {
            let self = this;
            let user = this.getUser;
            let pathExist = false;
            for (let i = 0; i < user.paths.length; i++) {
                let path = this.hyphenCase(user.paths[i].title);
                // If this Path has the same title as the selected Path
                if (path === this.$route.params.path) {
                  pathExist = true;
                    for (let j = 0; j < user.paths[i].courses.length; j++) {
                        let course = this.hyphenCase(user.paths[i].courses[j].title);
                        // If this Course has the same title as the selected Course
                        if (course === this.$route.params.course) {
                            // If the user has not watched any videos in this course...
                            if (!user.paths[i].courses[j].lastVideo) {
                                if (user.paths[i].courses[j].content.length) {
                                    let video = this.hyphenCase(user.paths[i].courses[j].content[0].title);
                                    this.$store.dispatch({
                                        type: 'setCurrentVideo',
                                        currentVideo: user.paths[i].courses[j].content[0],
                                        path: i,
                                        course: j,
                                        video: 0
                                    }).then(() => {
                                        self.push('/player/pathway/' + path + '/course/' + course + '/video/' + video + this.getTimestamp());
                                    }).catch((error) => {
                                        console.info("An error occurred while updating the cached user in the VueX store");
                                        console.log(error);
                                    })

                                }
                            } else {
                                for (let k = 0; k < user.paths[i].courses[j].content.length; k++) {
                                    let video = this.hyphenCase(user.paths[i].courses[j].content[k].title);
                                    if (this.hyphenCase(user.paths[i].courses[j].lastVideo.title) === video) {
                                        this.$store.dispatch({
                                            type: 'setCurrentVideo',
                                            currentVideo: user.paths[i].courses[j].lastVideo,
                                            path: i,
                                            course: j,
                                            video: k
                                        }).then(() => {
                                            self.push('/player/pathway/' + path + '/course/' + course + '/video/' + video + this.getTimestamp());
                                        }).catch((error) => {
                                            console.info("An error occurred while updating the cached user in the VueX store");
                                            console.log(error);
                                        })
                                    }

                                }
                            }
                        }
                    }
                }
            }
            if (!pathExist) {
              AXIOS.put('/api/users', {path: this.$route.params.path, username: user.username})
                  .then((response) => {
                    self.$store.dispatch({
                      type: 'setUser',
                      user: response.data
                    })
                    this.getVideo();
                  })
                  .catch((error) => {
                    console.log(error);
                  })
            }
        },
    },
    computed: {
        width() {
            return Math.sqrt(this.size / 10) + (this.size / 5);
        },
        ...mapGetters([
            'getUser',
            'getPaths'
        ])
    }
}
</script>

<style scoped>

</style>

<docs>
Used in determining which video to route the user to, and also handles timestamps.

---
</docs>