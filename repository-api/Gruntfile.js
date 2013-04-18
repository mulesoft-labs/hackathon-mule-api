module.exports = function ( grunt ) {

    'use strict';

    grunt.initConfig({

        pkg: grunt.file.readJSON('package.json'),

        meta: {
            specs: 'specs/**/*Spec.js'
        },

        simplemocha: {
            all: {
                src: '<%=meta.specs%>',
                options: {
                    timeout: 3000,
                    ignoreLeaks: false,
                    ui: 'bdd',
                    reporter: 'spec'
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-simple-mocha');


    grunt.registerTask('test', ['simplemocha']);

};