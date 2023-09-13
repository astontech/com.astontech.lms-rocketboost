<template>
  <div>
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
    <v-dialog v-model="dialog" max-width="290">
      <v-card>
        <v-card-title class="title">Remove Quiz?</v-card-title>
        <v-card-text>This will permanently remove this quiz from the database. This action cannot be undone.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" class="caption" text @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" class="caption" text @click="removeItem">Remove</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-container class="px-5">
      <v-data-table
          :headers="headers"
          :items="quizzes"
          :expanded.sync="expanded"
          :search="search"
          show-expand
          expand-icon="expand_more"
          class="elevation-1"
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>Quizzes</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-text-field
                v-model="search"
                append-icon="search"
                label="Search"
                single-line
                hide-details
            ></v-text-field>
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-btn icon v-on="on" @click="push('/quizzes/make')" v-if="isAdmin">
                  <v-icon>
                    add
                  </v-icon>
                </v-btn>
              </template>
              <span>Create Quiz</span>
            </v-tooltip>
          </v-toolbar>
        </template>
        <template v-slot:expanded-item="{ headers, item }" :id="item.id">
          <td :colspan="headers.length" style="padding: 0">
            <v-card flat style="border-radius: 0">
              <v-card-text>
                {{ item.description }}
              </v-card-text>
              <v-card-actions>
                <v-btn color="primary" dark text class="caption right" @click="goToQuiz(item)">Take Quiz</v-btn>
                <v-spacer></v-spacer>
                <div v-if="isAdmin">
                  <v-btn x-small icon @click="push('/quizzes/edit/' + item.id)">
                    <v-icon
                        small
                    >
                      edit
                    </v-icon>
                  </v-btn>

                  <v-btn x-small icon @click="promptDelete(item)">
                    <v-icon
                        small
                    >
                      delete
                    </v-icon>
                  </v-btn>
                </div>
              </v-card-actions>
            </v-card>
          </td>
        </template>
        <template v-slot:item.tags="{ item }">
          <v-chip small v-for="(tag, i) in item.tags"
                  :key="i"
                  class="ma-1">{{ tag }}
          </v-chip>
        </template>
      </v-data-table>
    </v-container>
  </div>
</template>

<script>
import common from '../../common.js'
import database from '../../firebaseInit.js'
import {mapGetters} from 'vuex'
import {AXIOS} from '../../http-commons'

export default {
  name: "QuizFinder",
  data() {
    return {
      alert: {
        active: false,
        message: '',
        color: undefined,
        btnColor: undefined,
        timeout: 0
      },
      dialog: false,
      expanded: [],
      expand: false,
      editedIndex: -1,
      editedItem: {
        title: '',
        description: '',
        tags: [],
        id: ''
      },
      defaultItem: {
        title: '',
        description: '',
        tags: [],
        id: ''
      },
      headers: [
        {
          text: 'Title',
          align: 'left',
          value: 'title',
        },
        {
          text: 'Tags',
          align: 'right',
          value: 'tags',
        }
      ],
      quizzes: [],
      rules: {
        required: value => !!value || 'Required.',
      },
      search: '',
    }
  },
  methods: {
    editItem(item) {
      this.editedIndex = this.quizzes.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialog = true;
    },
    promptDelete(item) {
      this.dialog = true;
      this.editedItem = item;
    },
    removeItem() {
      let self = this;
      let item = this.editedItem;
      AXIOS.delete("/api/quizzes/" + this.editedItem.id)
          .then(() => {
            this.quizzes = this.quizzes.filter(quiz => quiz.id !== item.id);
            self.snackbar('success', 'Successfully removed ' + item.title + '!', 2.5);
          })
          .catch((error) => {
            console.error(error);
            self.snackbar('error', 'An error occurred! ' + item.title + ' may not have been removed!', 2.5);
          })

      this.close();
    },
    close() {
      this.dialog = false;
      setTimeout(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      }, 300)
    },
    goToQuiz(item) {
      this.$store.dispatch({
        type: 'setQuizRedirectUrl',
        url: '/quizzes',
        path: 'quizzes'
      }).then(() => {
        this.$router.push('/quizzes/' + item.id);
      }).catch((error) => {
        console.log(error);
      })
    }
  },
  watch: {},
  created() {
    AXIOS.get("api/quizzes/")
        .then(response => {
          response.data.forEach(quiz => {
            this.quizzes.push({
              title: quiz.title,
              tags: quiz.tags,
              description: quiz.description,
              id: quiz.id
            })
          })
        }).catch(error => console.error(error));
  },
  mounted() {
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
    })
  },
  computed: {
    ...mapGetters([
      'getRole'
    ]),
    isAdmin() {
      return this.getRole === 'ADMIN'
    },
  }
}
</script>

<style scoped>
.right {
  float: right;
}
</style>

<docs>
Used for viewing all quizzes, and allows partial-CRUD functionality.

---
</docs>