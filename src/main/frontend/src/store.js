import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'
import {AXIOS} from './http-commons'

Vue.use(Vuex);

export default new Vuex.Store({
    plugins: [createPersistedState()],
    state: {
        currentVideo: null,
        user: {
            id: '',
            firstName: 'Developer',
            lastName: 'Support',
            username: 'developer.support',
            role: 'ADMIN',
            paths: [],
            notes: [],
            settings: {
                darkMode: false
            },
        },
        quiz: {
            redirectUrl: '',
            path: ''
        },
        processing: false
    },
    getters: {
        getQuizRedirect: state => state.quiz,
        getDarkMode: state => {
            if (state.user.settings) {
                return state.user.settings.darkMode
            }
        },
        getUser: state => state.user,
        getUsername: state => state.user.username,
        getUserPaths: state => state.user.paths,
        getName: state => {
            if (state.user.firstName && state.user.lastName) {
                let firstName = state.user.firstName.charAt(0).toUpperCase() + state.user.firstName.slice(1);
                let lastName = state.user.lastName.charAt(0).toUpperCase() + state.user.lastName.slice(1);
                return firstName + " " + lastName;
            }

            return ""
        },
        getEmail: state => state.user.username ? state.user.username + "@astontech.com" : 'NO USER',
        getRole: state => state.user.role,
        getCurrentVideo: state => state.currentVideo,
        getNotes: state => state.user.notes,
        isProcessing: state => state.processing,
    },
    mutations: {
        toggleDarkMode: (state, payload) => {
            state.user.settings.darkMode = payload.darkMode;
        },
        setQuizRedirectUrl: (state, payload) => {
            state.quiz.redirectUrl = payload.quizRedirectUrl;
            state.quiz.path = payload.path;
        },
        setUser: (state, payload) => {
            state.user = payload.user;
        },
        setCurrentVideo: (state, payload) => {
            state.currentVideo = payload.currentVideo;
        },
        setLastVideo: (state, payload) => {
            for (let i = 0; i < state.user.paths.length; i++) {
                if (state.user.paths[i].title === payload.path.title) {
                    // Set the last video completed in this course to the video at the indices provided
                    state.user.paths[i].courses[payload.courseIndex].lastVideo = payload.lastVideo;
                }
            }
        },
        setWatchingVideo: (state, payload) => {
            // tags the current video as completed and the current playback time (i.e. this video is NOT completed)
            state.user.paths[payload.pathIndex].courses[payload.courseIndex].content[payload.videoIndex].completed = true;
            state.user.paths[payload.pathIndex].courses[payload.courseIndex].content[payload.videoIndex].currentTime = payload.currentTime;
        },
        setVideoCompleted: (state, payload) => {
            // tags the current video as completed and resets the currentTime (i.e. this video HAS BEEN completed all the way thru)
            state.user.paths[payload.pathIndex].courses[payload.courseIndex].content[payload.videoIndex].completed = true;
            state.user.paths[payload.pathIndex].courses[payload.courseIndex].content[payload.videoIndex].currentTime = 0;
        },
        deleteUser: (state, payload) => {
            state.user = {
                id: '',
                firstName: '',
                lastName: '',
                username: '',
                role: '',
                notes: [],
                paths: [],
                settings: {
                    darkMode: false
                }
            }
            state.currentVideo = null;
        },
        addNote: (state, payload) => {
            state.user.notes.push({
                path: payload.path,
                course: payload.course,
                content: payload.content,
                note: payload.note,
                timestamp: payload.timestamp,
                date: payload.date
            })
        },
        updateNote: (state, payload) => {
            state.user.notes[payload.noteIndex] = payload.note
        },
        deleteNote: (state, payload) => {
            state.user.notes = state.user.notes.filter((note) => note !== payload.note)
        },
        setProcessing: (state, payload) => {
            state.processing = payload.processing;
        },
        setCurrentVideoAdditionalMaterials: (state, payload) => {
            state.currentVideo.materialsMarkdown = payload.additionalMaterials;
        }
    },
    actions: {
        setQuizRedirectUrl: (context, payload) => {
            context.commit({
                type: 'setQuizRedirectUrl',
                quizRedirectUrl: payload.url,
                path: payload.path
            })
        },
        setUser: (context, payload) => {
            context.commit({
                type: 'setUser',
                user: payload.user
            });
        },
        setCurrentVideo: (context, payload) => {
            context.commit({
                type: 'setCurrentVideo',
                currentVideo: payload.currentVideo
            });
        },
        setLastVideo: (context, payload) => {
            context.commit({
                type: 'setLastVideo',
                lastVideo: payload.lastVideo,
                videoIndex: payload.videoIndex,
                courseIndex: payload.courseIndex,
                path: payload.path
            })
        },
        setWatchingVideo: (context, payload) => {
            context.commit({
                type: 'setWatchingVideo',
                pathIndex: payload.pathIndex,
                courseIndex: payload.courseIndex,
                videoIndex: payload.videoIndex,
                currentTime: payload.currentTime
            })
        },
        setVideoCompleted: (context, payload) => {
            context.commit({
                type: 'setVideoCompleted',
                pathIndex: payload.pathIndex,
                courseIndex: payload.courseIndex,
                videoIndex: payload.videoIndex,
                currentTime: payload.currentTime
            })
        },
        addNote: (context, payload) => {
            context.commit({
                type: 'addNote',
                path: payload.path,
                course: payload.course,
                content: payload.content,
                note: payload.note,
                timestamp: payload.timestamp,
                date: payload.date
            })
        },
        toggleDarkMode: (context, payload) => {
            context.commit({
                type: 'toggleDarkMode',
                darkMode: payload.darkMode
            })
        },
        deleteUser: (context) => {
            context.commit({
                type: 'deleteUser'
            })
        },
        updateNote: (context, payload) => {
            context.commit({
                type: 'updateNote',
                noteIndex: payload.noteIndex,
                note: payload.note
            })
        },
        deleteNote: (context, payload) => {
            context.commit({
                type: 'deleteNote',
                note: payload.note
            })
        },
        setProcessing: (context, payload) => {
            context.commit({
                type: 'setProcessing',
                processing: payload.processing
            })
        },
        setCurrentVideoAdditionalMaterials: (context, payload) => {
            context.commit({
                type: 'setCurrentVideoAdditionalMaterials',
                additionalMaterials: payload.additionalMaterials
            })
        }
    }
})
