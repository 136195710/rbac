(function() {
    'use strict';

    angular
        .module('rbacApp')
        .controller('UserGroupController', UserGroupController);

    UserGroupController.$inject = ['$scope', '$state', 'UserGroup', 'UserGroupSearch'];

    function UserGroupController ($scope, $state, UserGroup, UserGroupSearch) {
        var vm = this;

        vm.userGroups = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            UserGroup.query(function(result) {
                vm.userGroups = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            UserGroupSearch.query({query: vm.searchQuery}, function(result) {
                vm.userGroups = result;
            });
        }    }
})();
