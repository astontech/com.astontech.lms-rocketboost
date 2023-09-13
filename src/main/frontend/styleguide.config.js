module.exports = {
    // set your styleguidist configuration here
    title: 'RocketBoost Docs',
    sections: [
        {
            name: 'Auth',
            components: 'src/components/auth/[A-Z]*.vue'
        },
        {
            name: 'Courses',
            components: [
                'src/components/courses/CourseBuilder.vue',
                'src/components/courses/CourseEditor.vue',
            ]
        },
        {
            name: 'Layout',
            components: [
                'src/components/layout/TopNavigation.vue',
                'src/components/misc/Rocketship.vue',
            ]
        },
        {
            name: 'Notes',
            components: [
                'src/components/core/Notes.vue',
            ]
        },
        {
            name: 'Paths',
            components: 'src/components/core/Pathways.vue'
        },
        {
            name: 'Quizzes',
            components: 'src/components/quizzes/[A-Z]*.vue'
        },
        {
            name: 'Videos',
            components: [
                'src/components/courses/VideoPlayer.vue',
                'src/components/courses/VideoProcessor.vue',
            ]
        },

    ],
    webpackConfig: {
        // custom config goes here
    },
}
