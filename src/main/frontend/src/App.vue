<template>
  <v-app :class="{'cap-height' : isVideo }">
    <top-navigation></top-navigation>
  </v-app>
</template>

<script>

import TopNavigation from "./components/layout/TopNavigation";
import {mapGetters} from 'vuex'

export default {
  name: 'App',
  components: {
    TopNavigation

  },
  data () {
    return {
      //
    }
  },
  created() {
    this.$vuetify.theme.dark = this.getDarkMode;
  },
  computed: {
    ...mapGetters([
      'getDarkMode'
    ]),
    isVideo() {
      return this.$route.name === 'Video';
    }
  }
}
</script>

<style lang="scss">

  @font-face {
    font-family: 'Raleway';
  }

  aside {
    div.v-navigation-drawer__content {
      border-right: 1px solid rgba(0,0,0,.12);
    }
  }

  textarea {
    resize: none;
  }

  div.plyr {
    max-height: 79.96vh; // Sets the max height of the video player (should be ~80% of the screen if I math'd right)
  }

  nav.borderBar {
    div.v-toolbar__content {
      border-bottom: 1px solid rgba(0,0,0,.12);

      div.layout.align-center {
        div.flex.xs3 {
          div.v-input__control {
            div.v-input__slot {
              background: rgba(0,0,0,.06);
            }
          }
        }
      }
    }
  }

  .v-btn-toggle {
    .theme--light.v-btn--active:hover::before, .theme--light.v-btn--active::before {
      opacity: 0;
    }
  }


  .round-card {
    border-radius: 0.75em;
  }

  ::-webkit-scrollbar {
    display: none;
    /*width: 8px;*/
    /*height: 8px;*/
    /*border-radius: 8px;*/
  }

  ::-webkit-scrollbar-button {
    display: none;
  }

  ::-webkit-scrollbar-thumb {
    background: #757575;
    border-radius: 8px;
  }

  ::-webkit-scrollbar-track {
    background: #e1e1e1;
  }

  /* Modify height of text in text field on Quiz Maker to be inline with label text */
  .adjust-height {
    .v-input__control {
      .v-input__slot {
        .v-text-field__slot {
          input {
            position: relative;
            top: 4px;
          }
        }
      }
    }
  }

  .underline {
    text-decoration: underline;
  }

  .v-timeline .v-timeline-item__dot {
    display: contents;
    background: none;
    box-shadow: none;
    -webkit-box-shadow: none;
  }

  .v-timeline-item__dot--small .v-timeline-item__inner-dot {
    width: 10px!important;
    height: 10px!important;
  }

  .v-timeline-item__dot--small .v-timeline-item__inner-dot .v-icon {
    font-size: 8px!important;
  }

  .v-progress-circular__info {
    font-size: .625rem;
    font-weight: 600;
    position: relative;
    top: 1px;
  }

  .v-progress-circular__underlay, .v-progress-circular__overlay {
    stroke-width: 5;
  }

  .v-timeline:before {
    height: calc(100% - 32px);
    width: 3px;
  }

  .light, .dark {
    .v-expansion-panel-content__wrap {
      padding: 0!important;
    }
  }

  .no-scroll {
    overflow-x: hidden;
  }

  .allow-scroll {
    overflow: auto!important;
  }

  .cap-height {
    /*overflow: hidden;*/
  }
</style>
