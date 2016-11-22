(function() {
    'use strict';

    angular
        .module('rbacApp')
        .controller('OperationController', OperationController);

    OperationController.$inject = ['$scope', '$state', 'Operation', 'OperationSearch'];

    function OperationController ($scope, $state, Operation, OperationSearch) {
        var vm = this;

        vm.operations = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Operation.query(function(result) {
                vm.operations = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            OperationSearch.query({query: vm.searchQuery}, function(result) {
                vm.operations = result;
            });
        }    }
})();
