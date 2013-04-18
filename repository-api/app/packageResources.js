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
        console.log('GET!!!');
        res.json(repo.find(function ( entry ) {

            return entry.groupId === groupId &&
                entry.artifactId === artifactId &&
                entry.version === version;

        }));
    });

    app.post('/packages/:groupId/:artifactId/:version', function ( req, res ) {
        repo.push(
            Object.merge({
                groupId: req.params.groupId,
                artifactId: req.params.artifactId,
                version: req.params.version
            }, req.body)
        );
        res.status(200);
    });


    /*    app.put('/packages/:groupId/:artifactId/:version', function (req, res) {

     });*/

};