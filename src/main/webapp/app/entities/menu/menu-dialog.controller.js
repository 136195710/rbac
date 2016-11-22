(function() {
    'use strict';

    angular
        .module('rbacApp')
        .controller('MenuDialogController', MenuDialogController);

    MenuDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Menu', 'Operation'];

    function MenuDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Menu, Operation) {
        var vm = this;

        vm.menu = entity;
        vm.clear = clear;
        vm.save = save;
        vm.operations = Operation.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.menu.id !== null) {
                Menu.update(vm.menu, onSaveSuccess, onSaveError);
            } else {
                Menu.save(vm.menu, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rbacApp:menuUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
