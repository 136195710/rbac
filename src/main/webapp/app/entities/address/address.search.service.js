(function() {
    'use strict';

    angular
        .module('rbacApp')
        .factory('AddressSearch', AddressSearch);

    AddressSearch.$inject = ['$resource'];

    function AddressSearch($resource) {
        var resourceUrl =  'api/_search/addresses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
