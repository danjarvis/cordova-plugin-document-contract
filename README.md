cordova-plugin-document-contract
================================

Android document / media provider plugin for [Cordova](http://cordova.apache.org)

Installation
------------

Install through the Cordova plugin repository:

`cordova plugin add cordova-plugin-document-contract`

Install directly from github:

`cordova plugin add https://github.com/danjarvis/cordova-plugin-document-contract.git`


Methods
-------

The plugin exposes two functions for working with a document/media provider.

```js
getContract({
    uri: 'content://com.foo.bar/xyz',  // content URI (required)
    columns: ['_display_name']         // projection of columns (optional)
  },
  success,                             // success callback
  error);                              // error callback
```

Resolves a content URI and retrieves a contract object.

An object of key/value pairs will get passed to the `success` callback (keys will be`columns` that returned a value).

If `columns` is not specified, the query will return all available columns. This is not recommended as it is [inefficient](http://developer.android.com/reference/android/content/ContentResolver.html#query%28android.net.Uri,%20java.lang.String[],%20java.lang.String,%20java.lang.String[],%20java.lang.String%29).


Documentation for columns of interest:

- [Document](https://developer.android.com/reference/android/provider/DocumentsContract.Document.html)
- [Media](http://developer.android.com/reference/android/provider/MediaStore.MediaColumns.html)
- [Images](http://developer.android.com/reference/android/provider/MediaStore.Images.ImageColumns.html)



```js
createFile({
    uri: 'content://com.foo.bar/xyz'	// content URI (required)
    fileName: 'file.pdf'                // name of output file (required)
  },
  success,                              // success callback
  error);                               // error callback
```

Resolves a content URI, retrieves corresponding file data and writes it to a file in the application's data directory (cordova.file.dataDirectory).


Usage
-----

For the below examples, assume that

1. `contentUri` is valid Content URI (e.g. coming from a Google Drive Send Intent).
2. The [Cordova File Plugin](http://plugins.cordova.io/#/package/org.apache.cordova.file) plugin is installed.

Get a full contract:
```js
window.plugins.DocumentContract.getContract({
    uri: contentUri
  },
  function(contract) {
    console.dir(contract);
    // Outputs
    // {
    //   '_display_name': 'SampleFile.pdf',
    //   'document_id': 'foo123',
    //   'last_modified': '/SomeDate/',
    //   'mime_type': 'application/pdf',
    //   'nth key': 'nth value'
    // }
  },
  function(error) {
    console.log('Error getting contract: ' + error);
  }
);
```

Get a specific contract:
```js
window.plugins.DocumentContract.getContract({
    uri: contentUri,
    columns: [
      '_display_name', 'mime_type', '_size'
    ]
  },
  function(contract) {
    console.dir(contract);
    // Outputs
    // {
    //   '_display_name': 'SampleFile.pdf',
    //   '_size': '5242880',
    //   'mime_type': 'application/pdf'
    // }
  },
  function(error) {
    console.log('Error getting contract: ' + error);
  }
);
```

Get file name and create file:
```js
window.plugins.DocumentContract.getContract({
    uri: contentUri,
      columns: [
      '_display_name'
    ]
  },
  function(contract) {
    window.plugins.DocumentContract.createFile({
        uri: contentUri,
        fileName: contract['_display_name'] || 'unknown'
      },
      function(fileName) {
        resolveLocalFileSystemURL(
          cordova.file.dataDirectory + fileName,
          function(fileEntry) {
            // Do something with FileEntry
          },
          function(error) {
            console.log('Error resolving file: ' + error);
          }
        );
      },
      function(error) {
        console.log('Error creating file: ' + error);
      }
    );
  },
  function(error) {
    console.log('Error getting contract: ' + error);
  }
);
```

License
-------

&copy; Dan Jarvis 2015, [MIT](http://danjarvis.mit-license.org)
