import Vue from 'vue'
import Router from 'vue-router'
// @ts-ignore
import store from './store.js'

Vue.use(Router);

const router = new Router({
    routes: [
        {
            path: '/',
            redirect: '/pathways'
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('./views/auth/LoginPage.vue')
        },
        {
            path: '/login/process',
            name: 'Login Processing',
            component: () => import('./views/auth/LoginProcessingPage.vue')
        },
        {
            path: '/notes',
            name: 'Notes',
            component: () => import('./views/core/NotesPage.vue')
        },
        {
            path: '/pathways',
            name: 'Pathways',
            component: () => import('./views/core/PathwaysPage.vue'),
        },
        {
            path: '/player/pathway/:path/course/:course/video/:video',
            name: 'Video',
            props: (route) => ({
                ...route.params
            }),
            component: () => import('./views/courses/VideoPage.vue')
        },
        {
            path: '/player/pathway/:path/course/:course/video',
            name: 'Video Processing',
            component: () => import('./views/courses/VideoProcessing.vue')
        },
        {
            path: '/pathways/:id/course/make',
            name: 'Course Builder',
            component: () => import('./views/courses/CourseMaking.vue'),
            beforeEnter(to, from, next) {
                if (store.getters.getRole === "ADMIN") {
                    next();
                }
                else {
                    next({ name: "Forbidden"})
                }
            }
        },
        {
            path: '/pathways/:pathId/course/:courseId/edit',
            name: 'Course Editor',
            component: () => import('./views/courses/CourseEditing.vue'),
            beforeEnter(to, from, next) {
                if (store.getters.getRole === "ADMIN") {
                    next();
                }
                else {
                    next({ name: "Forbidden"})
                }
            }
        },
        {
            meta: {title: 'Quizzes | Aston Interview Helper'},
            path: '/quizzes',
            name: 'quizzes',
            component: () => import('./views/quizzes/QuizViewAll.vue')
        },
        {
            meta: {title: 'Quiz Maker | Aston Interview Helper'},
            path: '/quizzes/make',
            name: 'quiz-maker',
            component: () => import('./views/quizzes/QuizCreate.vue'),
            beforeEnter(to, from, next) {
                if (store.getters.getRole === "ADMIN") {
                    next();
                }
                else {
                    next({ name: "Forbidden"})
                }
            }
        },
        {
            meta: {title: 'Taking Quiz | Aston Interview Helper'},
            path: '/quizzes/:id',
            name: 'quiz-taker',
            component: () => import('./views/quizzes/TakeQuiz.vue')
        },
        {
            meta: {title: 'Edit Quiz | Aston Interview Helper'},
            path: '/quizzes/edit/:id',
            name: 'quiz-editor',
            component: () => import('./views/quizzes/QuizEdit.vue'),
            beforeEnter(to, from, next) {
                if (store.getters.getRole === "ADMIN") {
                    next();
                }
                else {
                    next({ name: "Forbidden"})
                }
            }
        },
        {
            path: '/admin/panel',
            name: 'admin-panel',
            component: () => import('./views/admin/AdminPage.vue'),
            beforeEnter(to, from, next) {
                if (store.getters.getRole === "ADMIN") {
                    next();
                }
                else {
                    next({ name: "Forbidden"})
                }
            }
        },
        {
            path: '/forbidden',
            name: 'Forbidden',
            component: () => import('./views/misc/ForbiddenPage.vue')
        },
        {
            path: '*',
            name: 'Not Found',
            component: () => import('./views/misc/NotFoundPage.vue')
        }
    ],

});

export default router
