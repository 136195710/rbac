(function() {
    'use strict';

    angular
        .module('rbacApp')
        .controller('RoleController', RoleController);

    RoleController.$inject = ['$scope', '$state', 'Role', 'RoleSearch'];

    function RoleController ($scope, $state, Role, RoleSearch) {
        var vm = this;

        vm.roles = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Role.query(function(result) {
                vm.roles = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            RoleSearch.query({query: vm.searchQuery}, function(result) {
                vm.roles = result;
            });
        }    }
})();
