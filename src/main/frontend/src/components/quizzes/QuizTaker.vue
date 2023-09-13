<template>
    <div style="display: grid" class="quiz-column">
<!--        <div></div>-->
        <div class="ma-6" style="display: grid; justify-items: end; width: 90%">
            <v-card class="pt-2 pb-1 round-card card-size">
                <v-flex pt-3 pr-4 pl-4>

                    <!-- region Title -->
                    <v-flex pb-5>
                        <v-toolbar-title class="overline title">{{ quiz.title }}</v-toolbar-title>
                    </v-flex>
                    <!-- endregion -->
                </v-flex>
                <!-- endregion -->

                <v-divider></v-divider>
                <v-form ref="form">
                    <!-- region Questions -->
                    <v-expansion-panels mandatory :value="lastPanel">
                        <div v-for="(question, i) in quiz.questions" :key="i" class="width-100">
                            <!--region Question -->
                            <v-flex d-flex>
                                <v-card style="width: 100%" flat>
                                    <v-card-title class="pb-0" v-model="question.panel">
                                        <!--region SHOWN CONTENT GOES HERE -->
                                        <div>
                                            <!-- region Question & Question Type-->
                                            <v-flex d-inline-flex class="width-100">
                                                <span class="title mt-2 pt-3 pr-3 width-100">{{ question.title }}</span>
                                            </v-flex>
                                            <!-- endregion -->

                                        </div>
                                        <!-- endregion -->
                                    </v-card-title>
                                    <v-card-text>
                                        <!-- region HIDDEN CONTENT GOES HERE -->

                                        <!-- region IF QUESTION TYPE IS SINGLE CHOICE -->
                                        <v-flex v-if="question.questionType === 'SINGLE'">
                                            <v-radio-group v-model="singleSelection(question)[0]" required
                                                           :disabled="showAnswers">
                                                <v-radio v-for="(option, key) in question.options"
                                                         :key="key"
                                                         :label="option"
                                                         :value="option"
                                                         color="primary"></v-radio>
                                            </v-radio-group>
                                            <v-alert :type="getAlertType(question)" v-if="showAnswers">
                                                <div v-html="getAlertMessage(question)"></div>
                                            </v-alert>
                                        </v-flex>
                                        <!-- endregion -->
                                        <!--region IF QUESTION TYPE IS MULTIPLE -->
                                        <div v-if="question.questionType === 'MULTIPLE'">
                                            <v-flex pb-4>
                                                <v-checkbox style="margin-bottom: -35px;"
                                                            v-model="question.selected"
                                                            v-for="(option, key) in question.options"
                                                            :key="key"
                                                            :label="option"
                                                            :value="option"
                                                            color="primary"
                                                            multiple
                                                            :disabled="showAnswers"
                                                ></v-checkbox>
                                            </v-flex>
                                            <v-alert :type="getAlertType(question)" v-if="showAnswers">
                                                <div v-html="getAlertMessage(question)"></div>
                                            </v-alert>
                                        </div>

                                        <!-- endregion -->
                                    </v-card-text>
                                </v-card>
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
                    <v-flex>
                        <v-divider></v-divider>
                        <v-spacer></v-spacer>
                        <v-card-actions>
                            <v-flex px-3 v-if="showAnswers">
                                <small>Score: <span class="underline font-weight-bold"
                                                    :class="colorScore">{{ score }}</span>
                                    / {{ answerCount }}</small>
                            </v-flex>

                            <v-spacer></v-spacer>
                            <!--region Submit Quiz -->
                            <v-btn text class="caption" color="primary" @click.stop="submit">Submit</v-btn>

                            <v-dialog
                                    v-model="dialog"
                                    max-width="500"
                            >
                                <v-card>
                                    <v-card-title class="headline">{{ score }}/{{ answerCount }} Correct!
                                    </v-card-title>

                                    <v-card-actions>
                                        <v-spacer></v-spacer>

                                        <v-btn
                                                color="green darken-1"
                                                text
                                                @click="viewResults"
                                        >
                                            View Results
                                        </v-btn>

                                        <v-btn
                                                color="green darken-1"
                                                text
                                                @click="backToPreviousUrl"
                                        >
                                            {{ redirectToVideo ? 'Back to Course' : 'Back to Quizzes' }}
                                        </v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                            <!-- endregion -->
                        </v-card-actions>
                    </v-flex>
                </v-form>
            </v-card>
        </div>
        <div>
            <v-btn class="back-button" v-if="showBackToQuiz" color="primary"
                   @click.stop="backToPreviousUrl">
                {{ redirectToVideo ? 'Back to Course' : 'Back to Quizzes' }}
            </v-btn>
        </div>
    </div>
</template>

<script>
    import common from '../../common.js'
    import {AXIOS} from "../../http-commons";
    import {mapGetters} from "vuex";

    export default {
        name: "QuizTaker",
        data() {
            return {
                dialog: false,
                alert: {
                    active: false,
                    message: '',
                    color: undefined,
                    btnColor: undefined,
                    timeout: 0
                },
                quiz: {},
                rules: {
                    required: value => !!value || 'Required.',
                    checkboxRequired: value => !!value || 'Required.'
                },
                overlay: {
                    absolute: false,
                    opacity: 0.46,
                    overlay: false,
                    zIndex: 5,
                },
                search: "",
                showAnswers: false,
                answerCount: 0,
                showBackToQuiz: false,
                redirectToVideo: false
            }
        },
        methods: {
            backToPreviousUrl() {
                this.dialog = false;
                this.$router.push(this.getQuizRedirect.redirectUrl);
            },
            viewResults() {
                this.dialog = false;
                this.showBackToQuiz = true;
            },
            submit() {
                window.scroll({
                    top: 0,
                    left: 0,
                    behavior: 'smooth'
                });
                this.showAnswers = true;
                this.dialog = true;
                this.redirectToVideo = this.getQuizRedirect.path !== 'quizzes';
            },
            getCorrectCount(question) {
                let correctCount = 0;
                for (let s in question.selected) {
                    if (question.answers.includes(question.selected[s])) {
                        correctCount++;
                    }
                }
                return correctCount;
            },
            singleSelection(question) {
                if (question.selected === null) question.selected = [""];
                return question.selected;
            },
            getAlertType(question) {
                const correctCount = this.getCorrectCount(question);
                return question.selected === null || question.selected[0] === ""
                    ? 'warning'
                    : correctCount === question.answers.length
                        ? 'success'
                        : 'error';
            },
            getAlertMessage(question) {
                const correctCount = this.getCorrectCount(question);

                return question.selected == null || question.selected[0] === ""
                    ? 'No answer was provided for this question.'
                    : correctCount === question.answers.length
                        ? 'Correct!'
                        : this.incorrectAnswerMessage(question)
            },
            incorrectAnswerMessage(question) {
                let answers = question.answers;
                let html = '<div style="display: grid; grid-template-rows: repeat(answers.length+1, 1fr)">';
                html += '<span>Incorrect! You got ' + this.getCorrectCount(question) + ' out of ' + answers.length + ' correct</span>';
                for (let i = 0; i < answers.length; i++) {
                    html += '<span>';
                    html += (i + 1).toString() + ') ' + answers[i].toString();
                    html += '</span>'
                }
                html += '</div>';
                return html;
            }
        },
        computed: {
            ...mapGetters([
                'getQuizRedirect',
            ]),
            lastPanel() {
                if (this.quiz.questions) {
                    return this.quiz.questions.length - 1;
                }
                return 0;
            },
            score() {
                let score = 0;
                let question;

                if (this.quiz.questions !== undefined) {
                    for (let i = 0; i < this.quiz.questions.length; i++) {
                        question = this.quiz.questions[i];
                        score += this.getCorrectCount(question);
                    }
                }

                return score;
            },
            colorScore() {
                if ((this.score / this.answerCount) <= 0.5) {
                    return 'errorText'
                } else if ((this.score / this.answerCount) <= 0.75) {
                    return 'warningText'
                } else if ((this.score / this.answerCount) <= 1) {
                    return 'successText'
                }

                return 'errorText'
            }
        },
        watch: {},
        mounted() {
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

            AXIOS.get('api/quizzes/' + this.$route.params.id)
                .then(response => {
                    this.quiz = response.data;
                    console.log(this.quiz)
                    for (let i = 0; i < this.quiz.questions.length; i++) {
                        let question = this.quiz.questions[i];
                        this.answerCount += question.answers.length;
                    }
                }).catch(error => {
                console.error(error);
            })
        },
    }
</script>

<style scoped>
    .width-100 {
        width: 100% !important;
    }
    @media all and (min-width: 990px) {
        .quiz-column {
            grid-template-columns: 4fr 1fr;
        }
        .card-size {
            width: 70%;
        }
        .back-button {
            position: fixed;
            margin-top: 20px;
            margin-left: 20px;
        }
    }
    @media all and (max-width: 989px) and (min-width: 0px) {
        .quiz-column {
            grid-template-rows: auto auto;
            justify-items: center;
        }
        .card-size {
            width: 100%;
        }
        .back-button {
            margin-bottom: 20px;
        }
    }
</style>

<docs>
    Used for taking quizzes and grading them.

    ---
</docs>