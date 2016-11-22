(function() {
    'use strict';
    angular
        .module('rbacApp')
        .factory('UserGroup', UserGroup);

    UserGroup.$inject = ['$resource'];

    function UserGroup ($resource) {
        var resourceUrl =  'api/user-groups/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
