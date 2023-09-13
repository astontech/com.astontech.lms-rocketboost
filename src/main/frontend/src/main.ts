import Vue from 'vue'
import vuetify from './plugins/vuetify'
import App from './App.vue'
import router from './router'
// @ts-ignore
import VueKonami from 'vue-konami'
// @ts-ignore
import store from './store'

Vue.config.productionTip = false
Vue.config.devtools = true

Vue.use(VueKonami);

new Vue({
  router,
  // @ts-ignore
  store,
  //@ts-ignore
  vuetify,
  render: h => h(App)
}).$mount('#app')
