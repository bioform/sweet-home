<!DOCTYPE html>
<html lang="en" xmlns:ng="http://angularjs.org" id="ng-app" ng-app="sweetHome">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title><g:layoutTitle default="Sweet Home"/></title>

    <script>
        var webSocketLink = "${createLink(uri: '/stomp')}";
    </script>

    <!-- Bootstrap core CSS -->
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
    
    <g:layoutHead/>
  </head>

  <body>
    <div class="navbar navbar-fixed-top navbar-inverse" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Sweet Home</a>
        </div>
        <div class="collapse navbar-collapse" ng-controller="MenuCtrl">
          <ul class="nav navbar-nav">
            <li><a href="devices">Devices</a></li>
            <li><a href="scripts">Scripts</a></li>
            <li><a href="scheduler">Scheduler</a></li>
          </ul>
          <div class="navbar-form">
            <a href="" ng-model="showLogs" btn-checkbox class="btn btn-default">Logs</a>
          </div>
        </div><!-- /.nav-collapse -->
      </div><!-- /.container -->
    </div><!-- /.navbar -->

    <div class="container">

      <g:layoutBody/>

      <hr>

      <footer>
        <p>&copy; Company 2014</p>
      </footer>

    </div><!--/.container-->

    <div ng-controller="LogPanelCtrl" ng-show="showLogs">
        <div class="panel panel-default navbar-fixed-bottom">
          <div class="panel-heading">
              <a ng-click="hide()" href="" class="glyphicon glyphicon-remove pull-right"></a>
              <a ng-click="clear()" href="" class="glyphicon glyphicon-ban-circle pull-right" style="margin-right: 10px"></a>
              <h3 class="panel-title">Logs</h3>
          </div>
          <div class="panel-body" scroll-glue="true">
              <table class="table table-hover table-condensed">
                  <colgroup>
                      <col class="col-xs-1">
                      <col class="col-xs-7">
                  </colgroup>
                  <tbody>
                      <tr ng-repeat="log in logs" ng-class="log.level">
                          <td ng-bind="log.date | date:'yyyy-MM-dd HH:mm:ss'"></td>
                          <td ng-bind="log.msg"></td>
                      </tr>
                  </tbody>
              </table>
          </div>
        </div>
    </div>

  </body>
</html>
