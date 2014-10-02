<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>Sweet Home</title>
    <base href="${request.contextPath}/admin/">
  </head>

  <body>

        <div ng-view></div>

        <script>
            // global config
            var APP_CONFIG = ${config.encodeAsRaw()};
        </script>

  </body>
</html>