(function() {
    'use strict';

    angular
        .module('rbacApp')
        .controller('UserGroupDialogController', UserGroupDialogController);

    UserGroupDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'UserGroup', 'Role'];

    function UserGroupDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, UserGroup, Role) {
        var vm = this;

        vm.userGroup = entity;
        vm.clear = clear;
        vm.save = save;
        vm.roles = Role.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.userGroup.id !== null) {
                UserGroup.update(vm.userGroup, onSaveSuccess, onSaveError);
            } else {
                UserGroup.save(vm.userGroup, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rbacApp:userGroupUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
