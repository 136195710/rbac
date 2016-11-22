(function() {
    'use strict';

    angular
        .module('rbacApp')
        .controller('UserGroupDetailController', UserGroupDetailController);

    UserGroupDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserGroup', 'Role'];

    function UserGroupDetailController($scope, $rootScope, $stateParams, previousState, entity, UserGroup, Role) {
        var vm = this;

        vm.userGroup = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('rbacApp:userGroupUpdate', function(event, result) {
            vm.userGroup = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
