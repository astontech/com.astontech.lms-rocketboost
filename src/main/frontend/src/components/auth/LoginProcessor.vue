<template>
    <v-app class="grey lighten-4">
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
        <v-container fill-height align-center justify-center>
            <v-layout fill-height align-center justify-center>
                <v-progress-circular indeterminate color="primary" :size="size" :width="width"></v-progress-circular>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import common from '../../common'
    import {mapActions, mapGetters} from 'vuex'
    import {AXIOS} from '../../http-commons'
    export default {
        name: "LoginProcessor",
        data() {
            return {
                alert: {
                    active: false,
                    message: '',
                    color: undefined,
                    btnColor: undefined,
                    timeout: 0
                },
                size: 75
            }
        },
        mounted() {
            let self = this;
            this.push = common.push;
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

            // Get the current session user
            AXIOS.get('/api/users/session')
                .then((response) => {
                    // Store user information in vuex
                    self.$store.dispatch({
                        type: 'setUser',
                        user: response.data
                    }).then(() => {
                        // Reroute to /pathways
                        self.push("/pathways");
                    }).catch((error) => {
                        console.log(error);
                        self.snackbar('error', 'An error occurred while setting user details!', 2.5);
                    })
                })
                .catch((error) => {
                    console.log(error);
                    self.snackbar('error', 'An error occurred while getting the current user!', 2.5);
                })
        },
        computed: {
            /**
             * This corrects the width of the v-progress-circular so that the circle is not cut off on the edges
             * This issue MAY have been corrected upon upgrading to Vuetify 2.0.
             * @returns {number}
             */
            width() {
                return Math.sqrt(this.size / 10) + (this.size / 5);
            },
        }
    }
</script>

<style scoped>

</style>

<docs>
    Simple wrapper for Spring Security's success handler. Stores the session user in VueX.

    ---
</docs>