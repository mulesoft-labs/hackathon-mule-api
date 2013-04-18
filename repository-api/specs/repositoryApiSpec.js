var request = require('request'),
    expect = require('chai').expect;

function get( path, callback ) {
    return request.get({url: 'http://localhost:9090/api' + path, json: true}, callback);
}

function post( path, body, callback ) {
    return request.post({url: 'http://localhost:9090/api' + path, json: true, body: body}, callback);
}

describe('Mule Repository API', function () {


    it('returns an array of packages', function ( done ) {

        get('/packages', function ( error, response, body ) {
            expect(body).to.be.instanceOf(Array);
            done();
        });

    });

    it('can store a new package', function ( done ) {

        var testPackage = {
            name: 'Test',
            description: 'A test package',
            docsUrl: 'http://test.com'
        };

        post('/packages/testgroup/test/1.0', testPackage, function () {

            get('/packages/testgroup/test/1.0', function ( error, response, body ) {

                expect(body.name).to.equal(testPackage.name);
                expect(body.description).to.equal(testPackage.description);
                expect(body.docsUrl).to.equal(testPackage.docsUrl);

                done();
            });

        });

    });

});