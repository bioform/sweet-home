angular.module( 'scriptControllers', [

])

    .config(['$routeProvider', function config( $routeProvider ) {
        $routeProvider.
            when('/scripts', {
                templateUrl: '/assets/app/scripts/scripts.htm',
                controller: 'ScriptListCtrl',
                menu: 'scripts'
            }).
            when('/script/:script_id?', {
                templateUrl: '/assets/app/scripts/edit.htm',
                controller: 'ScriptEditCtrl',
                menu: 'scripts'
            })
    }])
    .controller('ScriptListCtrl', ['$scope','$filter', 'Script',
        function ($scope, $filter, Script) {

            $scope.scripts = Script.query();

            $scope.save = function save(script){ script.$save() };
            $scope.remove = function remove(script){
                script.$remove(function(data){
                    var index = $scope.scripts.indexOf(script);
                    $scope.scripts.splice(index, 1);
                });
            };

        }])
    .controller('ScriptEditCtrl', ['$scope','$filter', 'Script', '$routeParams', '$location', 'notificationService', '$http',
        function ($scope, $filter, Script, $routeParams, $location, notify, $http) {
            var id = $scope.id = $routeParams.script_id;
            if(angular.isUndefined(id) || id === null ) {
                $scope.script = new Script({code: "// Put your code here\n"});
            }
            else {
                $scope.script = Script.get({id: id});
            }

            $scope.save = function save(){
                $scope.script.$save(function(script){
                    if(script.id && !id){
                        $location.path('script/'+script.id);
                    } else {
                        notify.success("Script was saved")
                    }
                });
            };

            $scope.run = function run(){
                $http.get('/scripts/'+id+'/exec').
                    success(function(data, status, headers, config) {
                        console.log("executed!");
                    }).
                    error(function(data, status, headers, config) {
                        console.log("execution error");
                    });
            };

            $scope.cmOptions = {
                lineNumbers: true,
                matchBrackets: true,
                theme: 'monokai',
                lineWrapping : false,
                viewportMargin: Infinity,
                readOnly: false,
                mode: 'text/javascript'
            };

        }])
;
