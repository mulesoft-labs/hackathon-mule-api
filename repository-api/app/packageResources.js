require('sugar');

module.exports = function ( app ) {

    /*
     groupId
     artifactId
     version
     description;
     docsUrl;
     name;
     */
    var repo = [];

    app.get('/packages', function ( req, res ) {
        res.json(repo);
    });


    app.get('/packages/:groupId/:artifactId/:version', function ( req, res ) {
        var groupId = req.params.groupId,
            artifactId = req.params.artifactId,
            version = req.params.version;

        var req2 = repo.find(function ( entry ) {

            return entry.groupId === groupId &&
                entry.artifactId === artifactId &&
                entry.version === version;

        });

        res.json(req2);
    });

    app.post('/packages/:groupId/:artifactId/:version', function ( req, res ) {
        var newPackage = Object.merge({
            groupId: req.params.groupId,
            artifactId: req.params.artifactId,
            version: req.params.version
        }, req.body);

        repo.push(newPackage);

        res.json(newPackage);
    });


    /*    app.put('/packages/:groupId/:artifactId/:version', function (req, res) {

     });*/

};