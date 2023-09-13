import '@mdi/font/css/materialdesignicons.css'
import Vue from 'vue'
// @ts-ignore
import {Scroll} from 'vuetify/lib/directives'
// @ts-ignore
import Vuetify from 'vuetify/lib'
import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify, {
    directives: {
        Scroll
    }
})

export default new Vuetify({
    icons: {
        iconFont: 'mdi'
    },
    theme: {
        themes: {
            light: {
                primary: '#3EB3E0'
            },
            dark: {
                primary: '#F96816'
            }
        }
    }
})