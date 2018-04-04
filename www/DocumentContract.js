/**
 * Cordova Document Contract Plugin
 *
 * (c) Dan Jarvis 2015 :: License MIT
 */
(function(cordova){
  var DocumentContract = function() {
  };

  DocumentContract.prototype.createFile = function(params, success, error) {
    return cordova.exec(
      function(data) {
        if ('undefined' === typeof data || null === data)
          error('Failed to create file.');
        else
          success(data);
      }, function(err) {
        error(err);
      }, 'DocumentContract', 'createFile', [params]);
  };

  DocumentContract.prototype.getContract = function(params, success, error) {
    return cordova.exec(
      function(data) {
        if ('undefined' === typeof data || null === data)
          error('Failed to obtain contract.');
        else
          success(data);
      }, function(err) {
        error(err);
      }, 'DocumentContract', 'getContract', [params]);
  };

  window.DocumentContract = new DocumentContract();
  window.plugins = window.plugins || {};
  window.plugins.DocumentContract = window.DocumentContract;

})(window.Cordova || window.cordova)
