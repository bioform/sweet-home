//see: http://stackoverflow.com/questions/18368485/angular-js-resizable-div-directive
//it was little bit fixed(check for 'absolute' position was added)

angular.module('mc.resizer', []).directive('resizer', ['$document', function($document) {

    return function($scope, $element, $attrs) {

        $element.on('mousedown', function(event) {
            event.preventDefault();

            $document.on('mousemove', mousemove);
            $document.on('mouseup', mouseup);
        });

        function mousemove(event) {

            if ($attrs.resizer == 'vertical') {
                // Handle vertical resizer
                var x = event.pageX;

                if ($attrs.resizerMax && x > $attrs.resizerMax) {
                    x = parseInt($attrs.resizerMax);
                }

                if($element.css('position') == 'absolute') {
                    $element.css({
                        left: x + 'px'
                    });
                }

                $($attrs.resizerLeft).css({
                    width: x + 'px'
                });
                $($attrs.resizerRight).css({
                    left: (x + parseInt($attrs.resizerWidth)) + 'px'
                });

            } else {
                // Handle horizontal resizer
                var y = window.innerHeight - event.pageY;

                if($element.css('position') == 'absolute') {
                    $element.css({
                        bottom: y + 'px'
                    });
                }

                $($attrs.resizerTop).css({
                    bottom: (y + parseInt($attrs.resizerHeight)) + 'px'
                });
                $($attrs.resizerBottom).css({
                    height: y + 'px'
                });
            }
        }

        function mouseup() {
            $document.unbind('mousemove', mousemove);
            $document.unbind('mouseup', mouseup);
        }
    };
}]);
