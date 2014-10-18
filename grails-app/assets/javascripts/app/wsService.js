var wsService = angular.module('wsService', []);

wsService.factory('ws', [
    function(){
        var socket = new SockJS(window.webSocketLink);
        var client = Stomp.over(socket);

        return client;
    }]);
