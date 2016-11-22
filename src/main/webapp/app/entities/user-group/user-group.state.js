(function() {
    'use strict';

    angular
        .module('rbacApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-group', {
            parent: 'entity',
            url: '/user-group',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rbacApp.userGroup.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-group/user-groups.html',
                    controller: 'UserGroupController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userGroup');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-group-detail', {
            parent: 'entity',
            url: '/user-group/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rbacApp.userGroup.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-group/user-group-detail.html',
                    controller: 'UserGroupDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userGroup');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserGroup', function($stateParams, UserGroup) {
                    return UserGroup.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-group',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-group-detail.edit', {
            parent: 'user-group-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-group/user-group-dialog.html',
                    controller: 'UserGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserGroup', function(UserGroup) {
                            return UserGroup.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-group.new', {
            parent: 'user-group',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-group/user-group-dialog.html',
                    controller: 'UserGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                groupName: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-group', null, { reload: 'user-group' });
                }, function() {
                    $state.go('user-group');
                });
            }]
        })
        .state('user-group.edit', {
            parent: 'user-group',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-group/user-group-dialog.html',
                    controller: 'UserGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserGroup', function(UserGroup) {
                            return UserGroup.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-group', null, { reload: 'user-group' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-group.delete', {
            parent: 'user-group',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-group/user-group-delete-dialog.html',
                    controller: 'UserGroupDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserGroup', function(UserGroup) {
                            return UserGroup.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-group', null, { reload: 'user-group' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
