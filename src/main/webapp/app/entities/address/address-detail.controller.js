(function() {
    'use strict';

    angular
        .module('rbacApp')
        .controller('AddressDetailController', AddressDetailController);

    AddressDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Address', 'Person'];

    function AddressDetailController($scope, $rootScope, $stateParams, previousState, entity, Address, Person) {
        var vm = this;

        vm.address = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('rbacApp:addressUpdate', function(event, result) {
            vm.address = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
