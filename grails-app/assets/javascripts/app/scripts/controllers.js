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
    .controller('ScriptEditCtrl', ['$scope','$filter', 'Script', '$routeParams', '$ngSilentLocation', 'notificationService', '$http',
        function ($scope, $filter, Script, $routeParams, $ngSilentLocation, notify, $http) {
            var id = $scope.id = $routeParams.script_id;
            if(angular.isUndefined(id) || id === null ) {
                $scope.script = new Script({code: "// Put your code here\n"});
            }
            else {
                $scope.script = Script.get({id: id});
            }

            $scope.save = function save(form){
                var clearErrors = function clearErrors(form){
                    angular.forEach(form.$error, function(fields, type){
                        angular.forEach(fields, function(field){
                            field.$setValidity(type, true);
                        });
                    });
                };
                $scope.script.$save(
                    function(script) {
                        if (script.id && !id) {
                            id = $scope.id = $routeParams.script_id = script.id;
                            $ngSilentLocation.silent("/script/" + id);
                        }

                        form.$setPristine();
                        clearErrors(form);
                        notify.success("Script was saved");
                    },
                    function(response) {
                        if(response.status == 400) {
                            var errors = response.data;
                            angular.forEach(errors, function (err) {
                                var field = form[err.field];//err.field
                                if( field ){
                                    field.$setValidity(err.code, false)
                                    //err.message;
                                }
                            });
                            notify.error("Validation errors");
                        }
                        else {
                            notify.error("Server error");
                        }
                    }
                );
            };

            $scope.run = function run(){
                $http.get('/scripts/'+id+'/exec').
                    success(function(data, status, headers, config) {
                        //console.log("executed!");
                    }).
                    error(function(data, status, headers, config) {
                        //console.log("execution error");
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
