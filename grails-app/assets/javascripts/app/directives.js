angular.module( 'sweethome.inlineEdit', [

])
.directive('stopEvent', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            element.bind('click', function (e) {
                e.preventDefault();
                e.stopPropagation();
            });
        }
    };
})
.directive('editableInput', function () {
    return {
        restrict: 'C',
        link: function (scope, element, attr) {
            element.bind('click', function (e) {
                e.preventDefault();
                e.stopPropagation();
            });
        }
    };
});