var app = angular.module('sweetHome', [
    'ngResource',
    'ngSilent',
    'ngRoute',
    'angular-loading-bar',
    'localization',
    'ui.notify',
    'ui.bootstrap',
    'xeditable',
    'ui.codemirror',

    'deviceModel',
    'deviceControllers',
    'activeMenu',
    'locationModel',
    'commonDirectives'
]);

app.config(['$locationProvider', '$routeProvider', 'cfpLoadingBarProvider', 'notificationServiceProvider',
    function($locationProvider, $routeProvider, cfpLoadingBarProvider, notificationServiceProvider) {

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
