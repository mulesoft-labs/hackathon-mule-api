var express = require('express');
require('express-namespace');

var packageResources = require('./packageResources'),
    app = express(),
    port = (process.env.PORT) ? process.env.PORT : 9090;

app.use(express.compress());
app.use(express.bodyParser());

app.namespace('/api', function () {
    packageResources(app);
});


app.listen(port);

console.log("Running on port " + port);