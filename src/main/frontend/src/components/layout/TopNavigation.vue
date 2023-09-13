<template>
    <v-app v-konami:opts.custom="easterEgg">
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
        <div v-if="auth">
            <v-navigation-drawer
                    v-model="drawer"
                    permanent
                    app
                    mini-variant
                    v-if="displayable"
            >
                <v-list>
                    <v-list-item-group>
                        <v-flex mx-3 my-1 style="max-height: 62px;">
                            <v-layout align-center justify-center>
                                <svg class="navbar-logo" x="0px" y="0px" viewBox="0 0 100 125">
                                    <path :class="{'st0' : !darkMode, 'st1' : darkMode}"
                                          d="M85.1,80.7c-1.8-1.4-4-2.2-6.1-2.1c0.7-4.7-1.1-9.5-4.9-12.5c-3.5-2.8-8.1-3.7-12.3-2.3 c-0.2,0.1-0.4,0.1-0.6,0.2l8.3-14.4c1.4,0.6,2.8,1,4.3,1.1l2.7,9c0.2,0.5,0.6,0.9,1.1,1c0.1,0,0.2,0,0.3,0c0.4,0,0.9-0.2,1.1-0.6 l6.5-8.2c2.8-3.5,3.1-7.5,0.9-10.5l0.9-1.1c10.2-12.8,7.3-28.2,7.2-28.9c-0.1-0.7-0.8-1.2-1.6-1.2c-0.7,0.1-16.7,1.6-27.7,14.5 c-3.4-1.6-7.2-0.5-10.1,3.2l-6.4,8.2c-0.3,0.4-0.4,1-0.2,1.5s0.7,0.8,1.3,0.9l9.6,0.4c0.5,1.7,1.5,3.4,2.7,5L43.6,58.8 c-6.4-4.3-15.2-3.1-20.1,3.1c-1.8,2.3-2.9,5.1-3.2,8c-4.7-0.7-9.4,1.2-12.4,5c-1.4,1.7-2.1,4-2.5,5.2c-0.6,2.3-0.8,7.4,0.7,9.1 c0.3,0.3,0.7,0.5,1.1,0.5H87c0.4,0,0.8-0.2,1.1-0.4c0.3-0.3,0.4-0.7,0.4-1.1C88.5,86.8,87.4,82.6,85.1,80.7z M62,37.4 c-0.1-0.7-0.7-1.2-1.4-1.2l-7.8-0.3l4.6-5.9c1.1-1.3,3.9-4.2,7.3-1.9c0.6,0.4,1.5,0.3,2-0.3c8.5-10.6,21-13.5,25.1-14.2 c0.4,3.9,0.9,15.5-6.8,25.1l-1.6,2.1c-0.5,0.6-0.4,1.4,0.1,1.9c2.3,2.4,1.4,5.5-0.3,7.7l-4.8,6L76.2,49c-0.2-0.6-0.8-1.1-1.4-1.1 l0,0c-1.2,0-2.6-0.3-3.9-0.8c-0.9-0.4-1.8-0.8-2.6-1.4c-0.6-0.4-1.2-0.9-1.8-1.4c-0.8-0.6-1.5-1.4-2.1-2.1 C63.2,40.7,62.3,39,62,37.4z M8.2,86.7c-0.3-1.2-0.4-3.9,0.1-5.9c0.4-1.6,1.1-3.1,1.9-4.1c2.7-3.3,7-4.8,11.1-3.6 c0.5,0.1,0.9,0,1.3-0.3s0.6-0.7,0.6-1.2c-0.1-2.8,0.8-5.6,2.6-7.9c4.1-5.2,11.7-6,16.9-1.9c0.5,0.4,1.3,0.4,1.8,0L64,46.1 c0.8,0.8,1.7,1.4,2.7,2.1L55.2,68.3c-0.4,0.7-0.2,1.5,0.5,2c0.6,0.4,1.5,0.3,2-0.3c1.3-1.6,3-2.8,5-3.4c3.3-1,6.8-0.3,9.6,1.8 c3.4,2.7,4.8,7.4,3.4,11.6c-0.2,0.5,0,1.1,0.3,1.5c0.4,0.4,1,0.5,1.5,0.4c1.9-0.6,4.1-0.2,5.8,1.2c0.9,0.7,1.6,2.4,2,3.7L8.2,86.7 L8.2,86.7z M77.1,31.1c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M77.1,31.1c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M77.1,31.1 c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M84.6,22.5 c-1.5-1.2-3.4-1.7-5.3-1.5s-3.6,1.2-4.8,2.7c-2.5,3.1-2,7.6,1.1,10.1c1.3,1.1,2.9,1.6,4.5,1.6c1.1,0,2.1-0.2,3.1-0.7s1.8-1.1,2.6-2 C88.2,29.5,87.7,25,84.6,22.5z M83.5,30.8c-0.1,0.1-0.2,0.3-0.3,0.4c-0.2,0.2-0.5,0.4-0.8,0.6s-0.5,0.3-0.9,0.4 c-0.1,0-0.2,0.1-0.3,0.1c-0.1,0-0.2,0-0.3,0.1c-0.1,0-0.2,0-0.2,0c-0.1,0-0.2,0-0.3,0c-0.5,0-1.1,0-1.6-0.2c-0.1,0-0.3-0.1-0.4-0.2 c-0.1,0-0.2-0.1-0.3-0.2c-0.1-0.1-0.2-0.1-0.3-0.2c-0.1-0.1-0.2-0.1-0.3-0.2c-0.1-0.1-0.3-0.2-0.4-0.3c-0.1-0.1-0.2-0.2-0.3-0.3 c-0.1-0.1-0.2-0.2-0.2-0.3c0-0.1-0.1-0.1-0.1-0.2s-0.1-0.1-0.1-0.2s-0.1-0.2-0.1-0.3c0,0,0,0,0-0.1s-0.1-0.2-0.1-0.3v-0.1 c0-0.1-0.1-0.2-0.1-0.3c0-0.1,0-0.2-0.1-0.4c0-0.2,0-0.3,0-0.5c0-0.9,0.3-1.9,0.9-2.6c0.7-0.9,1.7-1.5,2.8-1.6c0.2,0,0.3,0,0.5,0 s0.5,0,0.7,0.1c0.2,0,0.5,0.1,0.7,0.2c0.4,0.2,0.9,0.4,1.2,0.7c1.1,0.8,1.6,2.1,1.6,3.3C84.4,29.1,84.1,30,83.5,30.8z M71.1,81.5 l-2.7,1.1c-0.8-2.1-2.5-3.6-4.7-4.1c-1.8-0.4-3.6-0.1-5.1,0.9c-0.6,0.4-1.5,0.2-2-0.4c-2.1-2.8-5.2-4.8-8.6-5.6 c-7.3-1.7-14.9,2.2-17.7,9.2l-2.7-1.1c3.3-8.4,12.3-13.1,21.1-11c3.7,0.9,7,2.9,9.5,5.7c1.9-0.9,4.1-1.1,6.2-0.6 C67.4,76.4,70,78.6,71.1,81.5z"/>
                                </svg>
                            </v-layout>
                        </v-flex>
                        <template v-for="(item, i) in items">
                            <v-subheader v-if="item.heading" :key="`heading-${i}`">{{ item.heading }}</v-subheader>

                            <v-divider v-else-if="item.divider" :key="`divider-${i}`"></v-divider>

                            <v-tooltip right v-else>
                                <template v-slot:activator="{ on }">
                                    <v-list-item @click="routeTo(item.route, item.name)" :key="i"
                                                 active-class="primary--text" v-on="on">

                                        <v-list-item-icon v-if="item.icon">
                                            <v-icon>{{ item.icon }}</v-icon>
                                        </v-list-item-icon>
                                    </v-list-item>
                                </template>
                                <span>{{ item.text }}</span>
                            </v-tooltip>
                        </template>
                    </v-list-item-group>
                </v-list>
            </v-navigation-drawer>
        </div>
        <v-app-bar v-if="displayable" class="borderBar" :height="topbarHeight" flat app
                   :scroll-off-screen=false>
            <v-layout align-center>
                <v-flex mx-3 my-1 style="max-height: 62px;" shrink v-if="!auth">
                    <v-layout align-center justify-start>
                        <svg class="navbar-logo-lg" x="0px" y="0px" viewBox="0 0 100 125">
                            <path :class="{'st0' : !darkMode, 'st1' : darkMode}"
                                  d="M85.1,80.7c-1.8-1.4-4-2.2-6.1-2.1c0.7-4.7-1.1-9.5-4.9-12.5c-3.5-2.8-8.1-3.7-12.3-2.3 c-0.2,0.1-0.4,0.1-0.6,0.2l8.3-14.4c1.4,0.6,2.8,1,4.3,1.1l2.7,9c0.2,0.5,0.6,0.9,1.1,1c0.1,0,0.2,0,0.3,0c0.4,0,0.9-0.2,1.1-0.6 l6.5-8.2c2.8-3.5,3.1-7.5,0.9-10.5l0.9-1.1c10.2-12.8,7.3-28.2,7.2-28.9c-0.1-0.7-0.8-1.2-1.6-1.2c-0.7,0.1-16.7,1.6-27.7,14.5 c-3.4-1.6-7.2-0.5-10.1,3.2l-6.4,8.2c-0.3,0.4-0.4,1-0.2,1.5s0.7,0.8,1.3,0.9l9.6,0.4c0.5,1.7,1.5,3.4,2.7,5L43.6,58.8 c-6.4-4.3-15.2-3.1-20.1,3.1c-1.8,2.3-2.9,5.1-3.2,8c-4.7-0.7-9.4,1.2-12.4,5c-1.4,1.7-2.1,4-2.5,5.2c-0.6,2.3-0.8,7.4,0.7,9.1 c0.3,0.3,0.7,0.5,1.1,0.5H87c0.4,0,0.8-0.2,1.1-0.4c0.3-0.3,0.4-0.7,0.4-1.1C88.5,86.8,87.4,82.6,85.1,80.7z M62,37.4 c-0.1-0.7-0.7-1.2-1.4-1.2l-7.8-0.3l4.6-5.9c1.1-1.3,3.9-4.2,7.3-1.9c0.6,0.4,1.5,0.3,2-0.3c8.5-10.6,21-13.5,25.1-14.2 c0.4,3.9,0.9,15.5-6.8,25.1l-1.6,2.1c-0.5,0.6-0.4,1.4,0.1,1.9c2.3,2.4,1.4,5.5-0.3,7.7l-4.8,6L76.2,49c-0.2-0.6-0.8-1.1-1.4-1.1 l0,0c-1.2,0-2.6-0.3-3.9-0.8c-0.9-0.4-1.8-0.8-2.6-1.4c-0.6-0.4-1.2-0.9-1.8-1.4c-0.8-0.6-1.5-1.4-2.1-2.1 C63.2,40.7,62.3,39,62,37.4z M8.2,86.7c-0.3-1.2-0.4-3.9,0.1-5.9c0.4-1.6,1.1-3.1,1.9-4.1c2.7-3.3,7-4.8,11.1-3.6 c0.5,0.1,0.9,0,1.3-0.3s0.6-0.7,0.6-1.2c-0.1-2.8,0.8-5.6,2.6-7.9c4.1-5.2,11.7-6,16.9-1.9c0.5,0.4,1.3,0.4,1.8,0L64,46.1 c0.8,0.8,1.7,1.4,2.7,2.1L55.2,68.3c-0.4,0.7-0.2,1.5,0.5,2c0.6,0.4,1.5,0.3,2-0.3c1.3-1.6,3-2.8,5-3.4c3.3-1,6.8-0.3,9.6,1.8 c3.4,2.7,4.8,7.4,3.4,11.6c-0.2,0.5,0,1.1,0.3,1.5c0.4,0.4,1,0.5,1.5,0.4c1.9-0.6,4.1-0.2,5.8,1.2c0.9,0.7,1.6,2.4,2,3.7L8.2,86.7 L8.2,86.7z M77.1,31.1c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M77.1,31.1c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M77.1,31.1 c0.1,0.1,0.2,0.2,0.4,0.3c0.1,0.1,0.2,0.1,0.3,0.2c-0.1-0.1-0.2-0.1-0.3-0.2C77.4,31.4,77.2,31.2,77.1,31.1z M84.6,22.5 c-1.5-1.2-3.4-1.7-5.3-1.5s-3.6,1.2-4.8,2.7c-2.5,3.1-2,7.6,1.1,10.1c1.3,1.1,2.9,1.6,4.5,1.6c1.1,0,2.1-0.2,3.1-0.7s1.8-1.1,2.6-2 C88.2,29.5,87.7,25,84.6,22.5z M83.5,30.8c-0.1,0.1-0.2,0.3-0.3,0.4c-0.2,0.2-0.5,0.4-0.8,0.6s-0.5,0.3-0.9,0.4 c-0.1,0-0.2,0.1-0.3,0.1c-0.1,0-0.2,0-0.3,0.1c-0.1,0-0.2,0-0.2,0c-0.1,0-0.2,0-0.3,0c-0.5,0-1.1,0-1.6-0.2c-0.1,0-0.3-0.1-0.4-0.2 c-0.1,0-0.2-0.1-0.3-0.2c-0.1-0.1-0.2-0.1-0.3-0.2c-0.1-0.1-0.2-0.1-0.3-0.2c-0.1-0.1-0.3-0.2-0.4-0.3c-0.1-0.1-0.2-0.2-0.3-0.3 c-0.1-0.1-0.2-0.2-0.2-0.3c0-0.1-0.1-0.1-0.1-0.2s-0.1-0.1-0.1-0.2s-0.1-0.2-0.1-0.3c0,0,0,0,0-0.1s-0.1-0.2-0.1-0.3v-0.1 c0-0.1-0.1-0.2-0.1-0.3c0-0.1,0-0.2-0.1-0.4c0-0.2,0-0.3,0-0.5c0-0.9,0.3-1.9,0.9-2.6c0.7-0.9,1.7-1.5,2.8-1.6c0.2,0,0.3,0,0.5,0 s0.5,0,0.7,0.1c0.2,0,0.5,0.1,0.7,0.2c0.4,0.2,0.9,0.4,1.2,0.7c1.1,0.8,1.6,2.1,1.6,3.3C84.4,29.1,84.1,30,83.5,30.8z M71.1,81.5 l-2.7,1.1c-0.8-2.1-2.5-3.6-4.7-4.1c-1.8-0.4-3.6-0.1-5.1,0.9c-0.6,0.4-1.5,0.2-2-0.4c-2.1-2.8-5.2-4.8-8.6-5.6 c-7.3-1.7-14.9,2.2-17.7,9.2l-2.7-1.1c3.3-8.4,12.3-13.1,21.1-11c3.7,0.9,7,2.9,9.5,5.7c1.9-0.9,4.1-1.1,6.2-0.6 C67.4,76.4,70,78.6,71.1,81.5z"/>
                        </svg>
                        <span class="pl-3 overline font-weight-bold logoText"
                              :class="{'rocketBlue' : !darkMode, 'rocketOrange' : darkMode}">ROCKETBOOST
                        </span>
                    </v-layout>
                </v-flex>
                <v-flex>
                    <v-layout align-center justify-start>
                        <span class="overline font-weight-bold logoText-lg"
                              :class="{'rocketBlue' : !darkMode, 'rocketOrange' : darkMode}" style="font-size: 14px!important;">ROCKETBOOST
                        </span>
                    </v-layout>
                </v-flex>
<!--                <v-flex xs3 xl3>-->
<!--                    <v-text-field-->
<!--                            prepend-inner-icon="search"-->
<!--                            label="Search"-->
<!--                            solo-->
<!--                            flat-->
<!--                            style="position: relative; top: 15px;"-->
<!--                    ></v-text-field>-->
<!--                </v-flex>-->
                <v-spacer></v-spacer>
            </v-layout>

            <v-toolbar-items>
                <v-btn text :ripple="false" v-if="auth">
                    <div>
                        <v-layout justify-end>
                            <span class="caption pa-0 normalCase">{{ getName }}</span>
                        </v-layout>
                        <div>
                            <v-subheader class="caption pa-0 normalCase" style="height: auto;">{{ getEmail }}</v-subheader>
                        </div>
                    </div>

                    <v-icon large class="px-2" :color="avatarColor">
                        mdi-account-circle
                    </v-icon>
                </v-btn>
                <v-layout align-center class="px-3" v-if="!auth" @click="routeTo('/login', 'Login')">
                    <a class="caption" :class="{'darkText' : !darkMode, 'lightText' : darkMode}"
                       style="text-transform: uppercase">
                        Log in
                    </a>
                </v-layout>
                <v-layout align-center class="px-3" v-if="!auth">
                    <v-btn class="caption" color="primary" @click="getUser()">
                        Get Started
                    </v-btn>
                </v-layout>

                <v-menu v-model="value" offset-y close-on-click close-on-content-click>
                    <template v-slot:activator="{ on }">
                        <v-btn icon :ripple="false" v-if="auth" v-on="on">
                            <v-icon>
                                mdi-dots-horizontal
                            </v-icon>
                        </v-btn>
                    </template>
                    <v-list>
                        <v-list-item @click="toggleDarkMode">
                            <v-icon>mdi-brightness-6</v-icon> &nbsp; Toggle dark mode
                        </v-list-item>
                        <v-list-item @click="push('/admin/panel')" v-if="konami.allow && isAdmin">
                            <v-icon>mdi-database-search</v-icon> &nbsp; Admin panel
                        </v-list-item>
                        <v-list-item @click="deleteUser" v-if="isDevelopment && isAdmin">
                            <v-icon>mdi-delete</v-icon> &nbsp; Delete cached user
                        </v-list-item>
                        <v-list-item @click="logout">
                            <v-icon>mdi-logout-variant</v-icon> &nbsp; Log out
                        </v-list-item>
                    </v-list>
                </v-menu>
            </v-toolbar-items>
        </v-app-bar>
        <v-content>
            <router-view></router-view>
        </v-content>
    </v-app>
</template>

<script>
    import {AXIOS} from '../../http-commons'
    import common from '../../common.js'
    import {mapGetters} from 'vuex'

    export default {
        data: () => ({
            // auth: false,
            alert: {
                active: false,
                message: '',
                color: undefined,
                btnColor: undefined,
                timeout: 0
            },
            drawer: null,
            konami: {
                allow: false
            },
            loading: false,
            opts: {
                timeout: 3000,
                chain: '38-38-40-40-37-39-37-39-66-65-13' // Up-Up-Down-Down-Left-Right-Left-Right-A-B-Enter
            },
            // JSON representation of the sidebar
            // In order for routing to work, each icon in items[] must have a route and name
            items: [
                {divider: true},
                {icon: 'mdi-school', text: 'Courses', route: '/pathways', name: 'Pathways'},
                {icon: 'mdi-notebook', text: "Notes", route: '/notes', name: 'Notes'},
                {icon: 'mdi-file-document-box-check', text: 'Quizzes', route: '/quizzes', name: 'Quizzes'}

            ],
            value: false,
            windowWidth: 0,
            windowHeight: 0
        }),
        methods: {
            /**
             * Triggers when `opts.chain` is activated. Enables admin panel.
             * @returns {void}
             * @public
             */
            easterEgg() {
                if (this.isAdmin) {
                    this.konami.allow = true;
                    this.snackbar('success', 'Admin panel enabled!', 2.5);
                }
            },
            /**
             * Removes the current user from the VueX store and logs them out of LDAP
             * @returns {void}
             * @public
             */
            logout() {
                let self = this;

                this.$store.dispatch({
                    type: 'deleteUser'
                }).then(() => {
                    self.push('/login?logout');
                }).catch((error) => {
                    console.log(error);
                })
            },
            /**
             * Toggles dark mode on/off and updates the user's account
             * @returns {void}
             * @public
             */
            toggleDarkMode() {
                let self = this;
                this.loading = true;

                this.$store.dispatch({
                    type: 'toggleDarkMode',
                    darkMode: !this.$vuetify.theme.dark
                }).then(() => {
                    self.$vuetify.theme.dark = self.getDarkMode;

                    AXIOS.post('/api/users', self.getUser)
                        .then((response) => {
                            self.loading = false;
                        })
                        .catch((error) => {
                            console.log(error);
                        })
                })
            },
            /**
             * Stores the current width of the window
             * @param {event} event Window Resized Event
             * @returns {void}
             * @public
             */
            getWindowWidth(event) {
                this.windowWidth = document.documentElement.clientWidth;
            },
            /**
             * Stores the current height of the window
             * @param {event} event Window Resized Event
             * @returns {Number}
             * @public
             */
            getWindowHeight(event) {
                this.windowHeight = document.documentElement.clientHeight;
            },
            /**
             * Removes the user from the VueX store
             * @returns {void}
             * @public
             */
            deleteUser() {
                this.$store.commit({
                    type: 'deleteUser'
                })
            }
        },
        mounted() {
            this.push = common.push;
            this.routeTo = common.routeTo;
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
            })

            this.$nextTick(function () {
                window.addEventListener('resize', this.getWindowWidth);
                window.addEventListener('resize', this.getWindowHeight);

                //Init
                this.getWindowWidth();
                this.getWindowHeight();
            })
        },
        beforeDestroy() {
            window.removeEventListener('resize', this.getWindowWidth);
            window.removeEventListener('resize', this.getWindowHeight);
        },
        computed: {
            /**
             * Returns true if there is an authenticated user stored in VueX
             * @returns {boolean}
             * @public
             */
            auth() {
                return !!this.getUser;
            },
            /**
             * Calculates and caches the height of the toolbar
             * @returns {Number}
             * @public
             */
            topbarHeight() {
                // placeholder for until authentication is done
                if (this.auth) {
                    return 61;
                } else {
                    return 83;
                }
            },
            /**
             * Returns true if the screen size is small, meaning that the navigation needs to shrink
             * @returns {boolean}
             * @public
             */
            miniNav() {
                let miniNavSize = 1600;
                return this.windowWidth < miniNavSize;
            },
            /**
             * Returns the state of `this.$vuetify.theme.dark`
             * @returns {boolean}
             * @public
             */
            darkMode() {
                return this.$vuetify.theme.dark;
            },
            /**
             * Returns a boolean denoting whether or not something should be displayed based on the current route
             * @returns {boolean}
             * @public
             */
            displayable() {
                switch (this.$route.name) {
                    case 'Login':
                    case 'Login Processing':
                    case 'Video':
                    case 'Video Processing':
                    case 'Not Found':
                    case 'Forbidden':
                        return false;
                    default:
                        return true;
                }
            },
            /**
             * Returns true if the role of the user is an admin
             * @returns {boolean}
             * @public
             */
            isAdmin() {
                return this.getRole === 'ADMIN'
            },
            /**
             * Returns true if the current environment is development
             * @returns {boolean}
             * @public
             */
            isDevelopment() {
                return process.env.NODE_ENV === 'development';
            },
            avatarColor() {
                return this.darkMode ? 'white' : 'grey darken-2';
            },
            ...mapGetters([
                'getUser',
                'getName',
                'getEmail',
                'getDarkMode',
                'getRole'
            ]),
        }
    }
</script>

<style lang="scss">

    .lightText {
        color: #fff;
    }

    .darkText {
        color: rgba(0, 0, 0, 0.87);
    }

    .v-navigation-drawer__border {
        display: none;
    }

    .navbar-logo {
        min-width: 35px;
        max-width: 35px;
    }

    .navbar-logo-lg {
        min-width: 50px;
        max-width: 50px;
        position: relative;
        top: 4px;
    }

    .pad-right {
        padding-right: 35px;
    }

    .st0 {
        fill: #3EB3E0;
    }

    .st1 {
        fill: #F96816;
    }

    .navItem {
        font-size: 13px !important;
    }

    .rocketBlue {
        color: #3EB3E0;
    }

    .rocketOrange {
        color: #F96816;
    }

    .logoText {
        font-size: 14px !important;
    }

    .normalCase {
        text-transform: none;
    }

</style>

<docs>
    Toolbar and side navigation bundled into one component. Houses some conditional logic for optionally displaying elements on the page.

    ---
</docs>