<template>
    <v-app>
        <v-container>
            <v-layout row justify-center>
                <v-flex xs8>
                    <v-card class="pt-4 pb-1 round-card">
                        <v-form v-model="valid" ref="form">
                            <!-- region Title & Header -->
                            <v-flex pt-3 pr-4 pl-4>
                                <!-- region Header -->
                                <v-flex pt-1 pb-4>
                                    <span class="subheading font-weight-medium">Create quiz</span>
                                </v-flex>
                                <!-- endregion -->

                                <!-- region Title -->
                                <v-flex pb-5>
                                    <v-text-field placeholder="Title" class="headline" v-model="quiz.title"
                                                  :rules="[rules.required]" tabindex="1"></v-text-field>
                                    <v-text-field placeholder="Description" v-model="quiz.description"
                                                  :rules="[rules.required]" tabindex="1"></v-text-field>
                                    <v-combobox multiple
                                                v-model="quiz.tags"
                                                append-icon
                                                small-chips
                                                deletable-chips
                                                placeholder="Quiz Tags"
                                                class="searchTags"
                                                :rules="[rules.required]"
                                                tabindex="1"
                                    >
                                    </v-combobox>
                                    <v-btn text color="primary" @click="save" class="float-right caption">
                                        Save Quiz
                                    </v-btn>
                                </v-flex>
                                <!-- endregion -->
                            </v-flex>
                            <!-- endregion -->

                            <v-divider class="mt-5"></v-divider>


                            <!-- region Questions -->
                            <v-expansion-panels mandatory :value="lastPanel">
                                <div v-for="(question, i) in quiz.questions" :key="i" class="width-100">
                                    <!--region Question -->
                                    <v-flex d-flex>
                                        <v-expansion-panel v-model="question.panel">
                                            <v-expansion-panel-header class="pb-0" expand-icon="">
                                                <!--region SHOWN CONTENT GOES HERE -->
                                                <div>
                                                    <!-- region Question & Question Type-->
                                                    <v-flex>
                                                        <v-textarea
                                                            placeholder="Question" rows="1"
                                                            single-line auto-grow v-model="question.title"
                                                            :rules="[rules.required]"
                                                            :tabindex="i + 1"
                                                        ></v-textarea>
                                                    </v-flex>
                                                    <v-flex d-inline-flex class="width-100">
                                                        <v-combobox multiple
                                                                    v-model="question.tags"
                                                                    append-icon
                                                                    small-chips
                                                                    deletable-chips
                                                                    placeholder="Question Tags"
                                                                    class="searchTags mt-2 pt-3 pr-3 width-100"
                                                                    :rules="[rules.required]"
                                                                    tabindex="1"
                                                        >
                                                        </v-combobox>
                                                        <v-select
                                                            :items="questionTypes"
                                                            filled
                                                            label="Select Question Type"
                                                            hint="Pick Multiple Choice or Single Choice"
                                                            v-model="question.questionType"
                                                            @click.native.stop
                                                            @change="question.answers = []"
                                                            class="questionTypes"
                                                        ></v-select>
                                                    </v-flex>
                                                    <!-- endregion -->

                                                </div>
                                                <!-- endregion -->
                                            </v-expansion-panel-header>
                                            <v-expansion-panel-content expand-icon="">
                                                <!-- region HIDDEN CONTENT GOES HERE -->

                                                <!-- region IF QUESTION TYPE IS SINGLE CHOICE -->
                                                <v-flex v-if="question.questionType === 'SINGLE'">
                                                    <div v-for="(option, j) in quiz.questions[i].options" :key="j">
                                                        <v-text-field :placeholder="'Option ' + (j + 1)"
                                                                      prepend-icon="radio_button_unchecked"
                                                                      v-model="question.options[j]"
                                                                      class="questionInput"
                                                                      @keydown.tab="addOption(question)"
                                                                      :tabindex="j + 1"
                                                                      :rules="[rules.required]"
                                                        ></v-text-field>
                                                        <v-tooltip bottom>
                                                            <template v-slot:activator="{ on }">
                                                                <v-btn icon v-on="on"
                                                                       @click.native.stop="removeOption(question, option)"
                                                                >
                                                                    <v-icon color="grey">close</v-icon>
                                                                </v-btn>
                                                            </template>
                                                            <span>Remove</span>
                                                        </v-tooltip>
                                                    </div>
                                                </v-flex>
                                                <!-- endregion -->
                                                <!--region IF QUESTION TYPE IS MULTIPLE -->
                                                <v-flex v-if="question.questionType === 'MULTIPLE'">
                                                    <div v-for="(option, j) in quiz.questions[i].options" :key="j">
                                                        <v-text-field :placeholder="'Option ' + (j + 1)"
                                                                      prepend-icon="check_box_outline_blank"
                                                                      v-model="question.options[j]"
                                                                      class="questionInput"
                                                                      @keydown.tab="addOption(question)"
                                                                      :tabindex="j + 1"
                                                                      :rules="[rules.required]"
                                                        ></v-text-field>
                                                        <v-tooltip bottom>
                                                            <template v-slot:activator="{ on }">
                                                                <v-btn icon v-on="on"
                                                                       @click="removeOption(question, option)">
                                                                    <v-icon color="grey">close</v-icon>
                                                                </v-btn>
                                                            </template>
                                                            <span>Remove</span>
                                                        </v-tooltip>
                                                    </div>
                                                </v-flex>
                                                <!-- endregion -->
                                                <div>
                                                    <!--region Add Option -->
                                                    <v-flex v-if="question.questionType === 'SINGLE'"
                                                            class="addOption">
                                                        <a @click="addOption(question)">
                                                            <v-text-field label="Add Option"
                                                                          prepend-icon="radio_button_unchecked"
                                                                          disabled></v-text-field>
                                                        </a>
                                                    </v-flex>
                                                    <v-flex v-if="question.questionType === 'MULTIPLE'"
                                                            class="addOption">
                                                        <a @click="addOption(question)">
                                                            <v-text-field label="Add Option"
                                                                          prepend-icon="check_box_outline_blank"
                                                                          disabled></v-text-field>
                                                        </a>
                                                    </v-flex>
                                                    <!-- endregion -->
                                                </div>

                                                <!-- endregion -->

                                                <!--region Actions -->
                                                <v-card-actions class="float-right" v-if="question.questionType !== ''">
                                                    <v-select v-if="question.questionType === 'Multiple'"
                                                              multiple
                                                              v-model="question.answers"
                                                              :items="quiz.questions[i].options"
                                                              style="width: 250px;"
                                                              :rules="[rules.required]"
                                                              label="Answer"
                                                    ></v-select>
                                                    <v-select v-else
                                                              v-model="question.answers"
                                                              :items="quiz.questions[i].options"
                                                              style="width: 250px;"
                                                              :rules="[rules.required]"
                                                              label="Answer"
                                                    ></v-select>
                                                    <v-tooltip bottom>
                                                        <template v-slot:activator="{ on }">
                                                            <v-btn icon v-on="on" @click="removeQuestion(question)"
                                                                   class="mx-1">
                                                                <v-icon color="grey">delete</v-icon>
                                                            </v-btn>
                                                        </template>
                                                        <span>Delete</span>
                                                    </v-tooltip>
                                                </v-card-actions>
                                                <!-- endregion -->
                                            </v-expansion-panel-content>
                                        </v-expansion-panel>
                                    </v-flex>

                                    <!-- endregion -->


                                    <!-- region Question Divider -->
                                    <v-flex v-if="quiz.questions.length > i + 1">
                                        <v-divider></v-divider>
                                    </v-flex>
                                    <!-- endregion -->
                                </div>
                            </v-expansion-panels>
                            <!-- endregion -->
                            <!--region Add Question -->
                            <v-flex>
                                <v-divider></v-divider>
                                <v-spacer></v-spacer>
                                <v-card-actions>
                                    <!--region Add Question -->
                                    <v-tooltip bottom>
                                        <template v-slot:activator="{ on }">
                                            <v-btn icon v-on="on" @click="addQuestion()" class="mx-1">
                                                <v-icon color="grey">add_circle</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Add question</span>
                                    </v-tooltip>
                                    <!-- endregion -->

                                </v-card-actions>
                            </v-flex>
                            <!-- endregion -->
                        </v-form>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
        <v-container fluid shrink>
            <v-layout justify-center>
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
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
import common from '../../common.js'
import {AXIOS} from "../../http-commons";

export default {
    name: "QuizMaker",
    data() {
        return {
            alert: {
                active: false,
                message: '',
                color: undefined,
                btnColor: undefined,
                timeout: 0
            },
            questionTypes: ['SINGLE', 'MULTIPLE'],
            quiz: {
                questions: [
                    {
                        options: [''],
                        questionType: '',
                        title: '',
                        panel: [0, 1],
                        answers: [],
                        selected: [],
                        tags: []
                    },
                ],
                tags: [],
                title: '',
                description: ''
            },
            rules: {
                required: value => !!value || 'Required.',
            },
            valid: false
        }
    },
    methods: {
        tabOption(val, question) {
            if (!val) {
                this.addOption(question);
            }
        },
        addOption(question) {
            let index = this.quiz.questions.indexOf(question);
            if (this.quiz.questions[index].options.length < 4) {
                question.options.push('')
            }
        },
        removeOption(question, option) {
            let questionIndex = this.quiz.questions.indexOf(question);
            let optionIndex = question.options.indexOf(option);

            if (this.quiz.questions[questionIndex].options.length > 1) {
                // Remove specified index from question's array of options
                this.quiz.questions[questionIndex].options.splice(optionIndex, 1);
            }
        },
        addQuestion() {
            this.quiz.questions.push({
                options: [''],
                questionType: '',
                title: '',
                panel: [0, 1],
                answers: [],
                selected: [],
                tags: []
            });
        },
        removeQuestion(question) {
            let index = this.quiz.questions.indexOf(question);

            if (this.quiz.questions.length > 1) {
                this.quiz.questions.splice(index, 1);
            }
        },
        save() {
            let self = this;
            if (this.$refs.form.validate()) {
                console.log(this.quiz)
                AXIOS.post('api/quizzes/', this.quiz)
                    .then((docRef) => {
                        self.snackbar('success', 'Quiz saved!', 2.5);
                        setTimeout(function () {
                            this.loading = false;
                            self.push('/quizzes')
                        }, 1500)
                    })
                    .catch((error) => {
                        self.snackbar('error', 'An error occurred while attempting to save the quiz.', 2.5);
                        console.error("Error adding document: ", error);
                    })
            } else {
                self.snackbar('warning', 'Please fill out all required fields!', 2.5)
            }
        }
    },
    computed: {
        lastPanel() {
            return this.quiz.questions.length - 1;
        }
    },
    watch: {},
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
}
</script>

<style scoped lang="scss">
.width-100 {
    width: 100% !important;
}

.float-right {
    float: right;
}

.questionInput {
    display: inline-flex;
    width: 91%;
}

.questionTypes {
    width: 41%;
}

.addOption {
    max-width: 122px;
}

</style>

<docs>
Used for creating new quizzes.

---
</docs>