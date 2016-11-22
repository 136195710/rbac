(function() {
    'use strict';

    angular
        .module('rbacApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('menu', {
            parent: 'entity',
            url: '/menu',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rbacApp.menu.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/menu/menus.html',
                    controller: 'MenuController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('menu');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('menu-detail', {
            parent: 'entity',
            url: '/menu/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rbacApp.menu.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/menu/menu-detail.html',
                    controller: 'MenuDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('menu');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Menu', function($stateParams, Menu) {
                    return Menu.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'menu',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('menu-detail.edit', {
            parent: 'menu-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/menu/menu-dialog.html',
                    controller: 'MenuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Menu', function(Menu) {
                            return Menu.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('menu.new', {
            parent: 'menu',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/menu/menu-dialog.html',
                    controller: 'MenuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                icon: null,
                                href: null,
                                plat: null,
                                level: null,
                                parentMenuId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('menu', null, { reload: 'menu' });
                }, function() {
                    $state.go('menu');
                });
            }]
        })
        .state('menu.edit', {
            parent: 'menu',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/menu/menu-dialog.html',
                    controller: 'MenuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Menu', function(Menu) {
                            return Menu.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('menu', null, { reload: 'menu' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('menu.delete', {
            parent: 'menu',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/menu/menu-delete-dialog.html',
                    controller: 'MenuDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Menu', function(Menu) {
                            return Menu.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('menu', null, { reload: 'menu' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
