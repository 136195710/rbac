(function() {
    'use strict';

    angular
        .module('rbacApp')
        .controller('MenuController', MenuController);

    MenuController.$inject = ['$scope', '$state', 'Menu', 'MenuSearch'];

    function MenuController ($scope, $state, Menu, MenuSearch) {
        var vm = this;

        vm.menus = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Menu.query(function(result) {
                vm.menus = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            MenuSearch.query({query: vm.searchQuery}, function(result) {
                vm.menus = result;
            });
        }    }
})();
