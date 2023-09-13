<template>
    <v-app>
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
        <v-dialog
                v-model="currentUserDialog"
                max-width="290"
        >
            <v-card>
                <v-card-title class="headline">Delete your account?</v-card-title>

                <v-card-text>
                    This will <strong>permanently remove your account</strong> from the database. This action <strong>cannot</strong> be undone.
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn
                            color="primary"
                            text
                            @click="close()"
                            class="caption"
                    >
                        Cancel
                    </v-btn>

                    <v-btn
                            color="primary"
                            text
                            @click="deleteUser()"
                            class="caption"
                    >
                        Delete
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog
                v-model="userDialog"
                max-width="290"
        >
            <v-card>
                <v-card-title class="headline">Delete all users?</v-card-title>

                <v-card-text>
                    This will <strong>permanently remove all users</strong> from the database. This action <strong>cannot</strong> be undone.
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn
                            color="primary"
                            text
                            @click="close()"
                            class="caption"
                    >
                        Cancel
                    </v-btn>

                    <v-btn
                            color="primary"
                            text
                            @click="deleteAllUsers()"
                            class="caption"
                    >
                        Delete
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog
                v-model="pathDialog"
                max-width="290"
        >
            <v-card>
                <v-card-title class="headline">Delete all paths?</v-card-title>

                <v-card-text>
                    This will <strong>permanently remove all paths</strong> from the database. This action <strong>cannot</strong> be undone.
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn
                            color="primary"
                            text
                            @click="close()"
                            class="caption"
                    >
                        Cancel
                    </v-btn>

                    <v-btn
                            color="primary"
                            text
                            @click="deleteAllPaths()"
                            class="caption"
                    >
                        Delete
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-container ma-0>
            <v-card>
                <v-card-title class="primary pb-3">
                    Admin Panel
                </v-card-title>
                <v-row
                        class="pa-4"
                        justify="space-between"
                >
                    <v-col cols="5">
<!--                        <v-btn class="caption" color="red" dark @click="promptDeleteUser()">-->
<!--                            <v-icon>mdi-alert-circle-outline</v-icon>-->
<!--                            <span class="mx-2">Delete Current User</span>-->
<!--                            <v-icon>mdi-alert-circle-outline</v-icon>-->
<!--                        </v-btn>-->
<!--                        <br>-->
<!--                        <br>-->
                        <v-btn class="caption" color="red" dark @click="promptDeleteUsers()">
                            <v-icon>mdi-alert-circle-outline</v-icon>
                            <span class="mx-2">Delete All Users</span>
                            <v-icon>mdi-alert-circle-outline</v-icon>
                        </v-btn>
                        <br>
                        <br>
                        <v-btn class="caption" color="red" dark @click="promptDeletePaths()">
                            <v-icon>mdi-alert-circle-outline</v-icon>
                            <span class="mx-2">Delete All Paths</span>
                            <v-icon>mdi-alert-circle-outline</v-icon>
                        </v-btn>
                    </v-col>
                </v-row>
            </v-card>
        </v-container>
    </v-app>
</template>

<script>
    import {AXIOS} from '../../http-commons'
    import {mapGetters} from 'vuex'
    import common from '../../common'
    export default {
        name: "AdminPanel",
        data() {
            return {
                alert: {
                    active: false,
                    message: '',
                    color: undefined,
                    btnColor: undefined,
                    timeout: 0
                },
                currentUserDialog: false,
                userDialog: false,
                pathDialog: false,
            }
        },
        methods: {
            promptDeleteUser() {
                this.currentUserDialog = true;
            },
            promptDeletePaths() {
                this.pathDialog = true;
            },
            promptDeleteUsers() {
                this.userDialog = true;
            },
            deleteUser() {
                let self = this;
                let username = this.getUsername;
                AXIOS.delete('/api/users/' + username)
                    .then((response) => {
                        self.$store.dispatch({
                            type: 'deleteUser'
                        }).then(() => {
                            self.snackbar('success', 'Your account has been deleted!', 2.5);
                            self.$router.push('/login');
                        })
                    })
                    .catch((error) => {
                        console.log(error);
                        self.snackbar('error', 'An error occurred! Your account may not have been deleted!', 2.5);
                    })
                this.close();
            },
            deleteAllUsers() {
                let self = this;
                AXIOS.delete('/api/users')
                    .then((response) => {
                        self.$store.dispatch({
                            type: 'deleteUser'
                        }).then(() => {
                            self.snackbar('success', 'All users have been deleted!', 2.5);
                            self.$router.push('/login');
                        })
                    })
                    .catch((error) => {
                        console.log(error);
                        self.snackbar('error', 'An error occurred! All users may not have been deleted!', 2.5);
                    })
                this.close();

            },
            deleteAllPaths() {
                let self = this;
                AXIOS.delete('/api/paths')
                    .then((response) => {
                        self.snackbar('success', 'All paths have been deleted!', 2.5);
                    })
                    .catch((error) => {
                        console.log(error);
                        self.snackbar('error', 'An error occurred! All paths may not have been deleted!', 2.5);
                    })
                this.close();
            },
            close() {
                this.currentUserDialog = false;
                this.userDialog = false;
                this.pathDialog = false;
            }
        },
        computed: {
            ...mapGetters([
                'getUsername'
            ])
        },
        mounted() {
            this.snackbar = common.snackbar;
            this.snackbar('success', 'Hello world!', 2.5);
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
        }
    }
</script>

<style scoped>

</style>