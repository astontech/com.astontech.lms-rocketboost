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

        <v-layout :class="{'splashbg-light' : !darkMode, 'splashbg-dark' : darkMode}">
            <v-flex xs4 :class="{'bg-light' : !darkMode, 'bg-dark' : darkMode}">
                <v-layout d-block align-center justify-center pa-5>
                    <v-layout align-center justify-center>
                        <svg class="navbar-logo-xl" x="0px" y="0px" viewBox="0 0 100 125">
                            <path :class="{'st0' : !darkMode, 'st1' : darkMode}"
                                  d="M85.1,80.7c-1.8-1.4-4-2.2-6.1-2.1c0.7-4.7-1.1-9.5-4.9-12.5c-3.5-2.8-8.1-3.7-12.3-2.3 c-0.2,0.1-0.4,0.1-0.6,0.2l8.3-14.4c1.4,0.6,2.8,1,4.3,1.1l2.7,9c0.2,0.5,0.6,0.9,1.1,1c0.1,0,0.2,0,0.3,0c0.4,0,0.9-0.2,1.1-0.6 l6.5-8.2c2.8-3.5,3.1-7.5,0.9-10.5l0.9-1.1c10.2-12.8,7.3-28.2,7.2-28.9c-0.1-0.7-0.8-1.2-1.6-1.2c-0.7,0.1-16.7,1.6-27.7,14.5 c-3.4-1.6-7.2-0.5-10.1,3.2l-6.4,8.2c-0.3,0.4-0.4,1-0.2,1.5s0.7,0.8,1.3,0.9l9.6,0.4c0.5,1.7,1.5,3.4,2.7,5L43.6,58.8 c-6.4-4.3-15.2-3.1-20.1,3.1c-1.8,2.3-2.9,5.1-3.2,8c-4.7-0.7-9.4,1.2-12.4,5c-1.4,1.7-2.1,4-2.5,5.2c-0.6,2.3-0.8,7.4,0.7,9.1 c0.3,0.3,0.7,0.5,1.1,0.5H87c0.4,0,0.8-0.2,1.1-0.4c0.3-0.3,0.4-0.7,0.4-1.1C88.5,86.8,87.4,82.6,85.1,80.7z M62,37.4 c-0.1-0.7-0.7-1.2-1.4-1.2l-7.8-0.3l4.6-5.9c1.1-1.3,3.9-4.2,7.3-1.9c0.6,0.4,1.5,0.3,2-0.3c8.5-10.6,21-13.5,25.1-14.2 c0.4,3.9,0.9,15.5-6.8,25.1l-1.6,2.1c-0.5,0.6-0.4,1.4,0.1,1.9c2.3,2.4,1.4,5.5-0.3,7.7l-4.8,6L76.2,49c-0.2-0.6-0.8-1.1-1.4-1.1 l0,0c-1.2,0-2.6-0.3-3.9-0.8c-0.9-0.4-1.8-0.8-2.6-1.4c-0.6-0.4-1.2-0.9-1.8-1.4c-0.8-0.6-1.5-1.4-2.1-2.1 C63.2,40.7,62.3,39,62,37.4z M8.2,86.7c-0.3-1.2-0.4-3.9,0.1-5.9c0.4-1.6,1.1-3.1,1.9-4.1c2.7-3.3,7-4.8,11.1-3.6 c0.5,0.1,0.9,0,1.3-0.3s0.6-0.7,0.6-1.2c-0.1-2.8,0.8-5.6,2.6-7.9c4.1-5.2,11.7-6,16.9-1.9c0.5,0.4,1.3,0.4,1.8,0L64,46.1 c0.8,0.8,1.7,1.4,2.7,2.1L55.2,68.3c-0.4,0.7-0.2,1.5,0.5,2c0.6,0.4,1.5,0.3,2-0.3c1.3-1.6,3-2.8,5-3.4c3.3-1,6.8-0.3,9.6,1.8 c3.4,2.7,4.8,7.4,3.4,11.6c-0.2,0.5,0,1.1,0.3,1.5c0.4,0.4,1,0.5,1.5,0.4c1.9-0.6,4.1-0.2,5.8,1.2c0.9,0.7,1.6,2.4,2,3.7L8.2,86.7 L8.2,86.7z M77.1,31.1c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M77.1,31.1c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M77.1,31.1 c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M84.6,22.5 c-1.5-1.2-3.4-1.7-5.3-1.5s-3.6,1.2-4.8,2.7c-2.5,3.1-2,7.6,1.1,10.1c1.3,1.1,2.9,1.6,4.5,1.6c1.1,0,2.1-0.2,3.1-0.7s1.8-1.1,2.6-2 C88.2,29.5,87.7,25,84.6,22.5z M83.5,30.8c-0.1,0.1-0.2,0.3-0.3,0.4c-0.2,0.2-0.5,0.4-0.8,0.6s-0.5,0.3-0.9,0.4 c-0.1,0-0.2,0.1-0.3,0.1c-0.1,0-0.2,0-0.3,0.1c-0.1,0-0.2,0-0.2,0c-0.1,0-0.2,0-0.3,0c-0.5,0-1.1,0-1.6-0.2c-0.1,0-0.3-0.1-0.4-0.2 c-0.1,0-0.2-0.1-0.3-0.2c-0.1-0.1-0.2-0.1-0.3-0.2c-0.1-0.1-0.2-0.1-0.3-0.2c-0.1-0.1-0.3-0.2-0.4-0.3c-0.1-0.1-0.2-0.2-0.3-0.3 c-0.1-0.1-0.2-0.2-0.2-0.3c0-0.1-0.1-0.1-0.1-0.2s-0.1-0.1-0.1-0.2s-0.1-0.2-0.1-0.3c0,0,0,0,0-0.1s-0.1-0.2-0.1-0.3v-0.1 c0-0.1-0.1-0.2-0.1-0.3c0-0.1,0-0.2-0.1-0.4c0-0.2,0-0.3,0-0.5c0-0.9,0.3-1.9,0.9-2.6c0.7-0.9,1.7-1.5,2.8-1.6c0.2,0,0.3,0,0.5,0 s0.5,0,0.7,0.1c0.2,0,0.5,0.1,0.7,0.2c0.4,0.2,0.9,0.4,1.2,0.7c1.1,0.8,1.6,2.1,1.6,3.3C84.4,29.1,84.1,30,83.5,30.8z M71.1,81.5 l-2.7,1.1c-0.8-2.1-2.5-3.6-4.7-4.1c-1.8-0.4-3.6-0.1-5.1,0.9c-0.6,0.4-1.5,0.2-2-0.4c-2.1-2.8-5.2-4.8-8.6-5.6 c-7.3-1.7-14.9,2.2-17.7,9.2l-2.7-1.1c3.3-8.4,12.3-13.1,21.1-11c3.7,0.9,7,2.9,9.5,5.7c1.9-0.9,4.1-1.1,6.2-0.6 C67.4,76.4,70,78.6,71.1,81.5z"/>
                        </svg>
                        <span class="pl-3 overline font-weight-medium logoText-xl"
                              :class="{'rocketBlue' : !darkMode, 'rocketOrange' : darkMode}"
                        >ROCKETBOOSTTTTTTT
                        </span>
                    </v-layout>
                    <v-layout justify-center>
                        <v-flex xs8>
                            <form method="post" action="/login">
                                <div>
                                    <label class="font-weight-medium"
                                        
                                        style="color: red;"
                                        :class="{'whiteText' : darkMode}">
                                        {{ routeParam }} - Incorrect Email or Password - {{ this.$route.params}}
                                    </label>
                                </div>
                                <label class="font-weight-medium" :class="{'whiteText' : darkMode}">Email</label>
                                <v-text-field solo flat v-model="email" id="username" name="username" :rules="[rules.required]"></v-text-field>
                                <label class="font-weight-medium" :class="{'whiteText' : darkMode}">Password</label>
                                <v-text-field solo flat v-model="password" id="password" name="password" type="password" :rules="[rules.required]"
                                ></v-text-field>
                                <v-btn block class="caption normalText" color="primary"
                                       :loading="loading" type="submit">Sign in
                                </v-btn>
                            </form>
                        </v-flex>
                    </v-layout>
                </v-layout>
            </v-flex>
            <v-layout justify-center>
                <v-flex xs10 align-content-center>
                    <v-layout fill-height align-center justify-center>
                        <rocketship></rocketship>
                    </v-layout>
                </v-flex>
            </v-layout>
        </v-layout>
    </v-app>
</template>

<script>
    import axios from 'axios'
    import {AXIOS} from '../../http-commons'
    import common from '../../common'
    import Rocketship from "../misc/Rocketship";

    export default {
        name: "Login",
        components: {Rocketship},
        data() {
            return {
                alert: {
                    active: false,
                    message: '',
                    color: undefined,
                    btnColor: undefined,
                    timeout: 0
                },
                email: '',
                password: '',
                loading: false,
                show: false,
                rules: {
                    required: value => !!value || 'Required.',
                },
                routeParam: ''
            }
        },
        /**
         * Called when the page is finished rendering
         */
        mounted() {
            this.push = common.push;
            this.log = common.log;
            this.snackbar = common.snackbar;

            console.log(this.$route.params);
            this.routeParam = this.$route.params;
            /**
             * Open snackbar event
             * @type {object}
             */
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


            // Spring Security messes up font imports on the login page.
            // Reloading the page resolves this. The code below will
            // reload the page exactly one time.
            if (localStorage.getItem('reloaded')) {
                localStorage.removeItem('reloaded');
            }
            else {
                localStorage.setItem('reloaded', '1');
                location.reload();
            }
        },
        computed: {
            /**
             * Returns true or false if dark mode is enabled or disabled respectively
             * @returns {boolean}
             * @public
             */
            darkMode() {
                return this.$vuetify.theme.dark;
            },
        },
    }
</script>

<style scoped>
    label {
        font-size: 13px !important;
    }

    .whiteText {
        color: white;
    }

    .bg-light {
        background: rgb(215, 233, 255);
        /*max-height: 442.66px;*/
    }

    .bg-dark {
        background: #212121
        /*max-height: 442.66px;*/
    }

    .splashbg-light {
        background: #f1f1f1;
    }

    .splashbg-dark {
        background: #303030;
    }

    .navbar-logo-xl {
        min-width: 75px;
        max-width: 75px;
    }

    .logoText-xl {
        font-size: 28px !important;
    }

    .normalText {
        text-transform: none;
    }

    input:-internal-autofill-selected {
        background-color: inherit!important;
    }
</style>

<docs>
    Simple form wrapper for Spring Security's login page.

    ---
</docs>