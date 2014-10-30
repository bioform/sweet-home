var app = angular.module('sweetHome', [
    'ngResource',
    'ngSilent',
    'ngRoute',
    'angular-loading-bar',
    'pascalprecht.translate',
    'ui.notify',
    'ui.bootstrap',
    'xeditable',
    'ui.codemirror',
    'luegg.directives',

    'deviceModel',
    'deviceControllers',
    'activeMenu',
    'locationModel',
    'scriptModel',
    'scriptControllers',
    'wsService',
    'logsPanel',
    'sweethome.inlineEdit',
    'sweethome.directives',
    'measurementsCharts'
]);

app.config(['$locationProvider', '$routeProvider', 'cfpLoadingBarProvider', 'notificationServiceProvider', '$translateProvider',
    function($locationProvider, $routeProvider, cfpLoadingBarProvider, notificationServiceProvider, $translateProvider) {

        // ajax loading bar
        cfpLoadingBarProvider.includeSpinner = true;

        // route
        $locationProvider.html5Mode(true);

        $routeProvider.otherwise({
                redirectTo: '/devices'
            });

        // notifier settings
        var stack_bottomright = {"dir1": "up", "dir2": "left", "push": "top", "firstpos1": 25, "firstpos2": 25};
        notificationServiceProvider.setDefaults({
            history: false,
            delay: 4000,
            styling: 'bootstrap3',
            addclass: "stack-bottomright",
            stack: stack_bottomright
        });

        $translateProvider
            .useStaticFilesLoader({
                prefix: '/assets/i18n/',
                suffix: '.json'
            })
            .registerAvailableLanguageKeys(['en', 'ru'], {
                'en_US': 'en',
                'en_UK': 'en',
                'ru_RU': 'ru'
            })
            .determinePreferredLanguage()
            .fallbackLanguage('en');

    }]);

/**
 * see: http://stackoverflow.com/questions/20663076/angularjs-app-run-documentation
 */
app.run(['editableOptions', 'editableThemes', function(editableOptions, editableThemes) {
    // x-editable
    editableThemes.bs3.inputClass = 'input-sm';
    editableThemes.bs3.buttonsClass = 'btn-sm';
    editableOptions.theme = 'bs3';
}]);
