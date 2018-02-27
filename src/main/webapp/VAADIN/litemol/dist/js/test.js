/*
 * Copyright (c) 2016 - now David Sehnal, licensed under Apache 2.0, See LICENSE file for more info.
 */
var LiteMol;
(function (litemolid) {
    var AngularExample;
    (function (AngularExample) {
        var Plugin = LiteMol.Plugin;
        var Views = Plugin.Views;
        var Bootstrap = LiteMol.Bootstrap;
        var Transformer = Bootstrap.Entity.Transformer;
        var LayoutRegion = Bootstrap.Components.LayoutRegion;
        function createPlugin(target) {
            var customSpecification = {
                settings: {
                    'density.defaultVisualBehaviourRadius': 2
                },
                transforms: [
                    { transformer: Transformer.Molecule.CreateVisual, view: Views.Transform.Molecule.CreateVisual },
                    { transformer: Transformer.Density.CreateVisualBehaviour, view: Views.Transform.Density.CreateVisualBehaviour },
                ],
                behaviours: [
                    // you will find the source of all behaviours in the Bootstrap/Behaviour directory
                    // usually keep these 2
                    Bootstrap.Behaviour.SetEntityToCurrentWhenAdded,
                    Bootstrap.Behaviour.FocusCameraOnSelect,
                    // this colors the visual when it's selected by mouse or touch
                    Bootstrap.Behaviour.ApplyInteractivitySelection,
                    // this shows what atom/residue is the pointer currently over
                    Bootstrap.Behaviour.Molecule.HighlightElementInfo,
                    // distance to the last "clicked" element
                    Bootstrap.Behaviour.Molecule.DistanceToLastClickedElement,
                    // when the same element is clicked twice in a row, the selection is emptied
                    Bootstrap.Behaviour.UnselectElementOnRepeatedClick,
                    // when somethinh is selected, this will create an "overlay visual" of the selected residue and show every other residue within 5ang
                    // you will not want to use this for the ligand pages, where you create the same thing this does at startup
                    Bootstrap.Behaviour.Molecule.ShowInteractionOnSelect(5),
                    // this tracks what is downloaded and some basic actions. Does not send any private data etc.
                    // While it is not required for any functionality, we as authors are very much interested in basic 
                    // usage statistics of the application and would appriciate if this behaviour is used.
                    Bootstrap.Behaviour.GoogleAnalytics('UA-77062725-1')
                ],
                components: [
                    Plugin.Components.Visualization.HighlightInfo(LayoutRegion.Main, true),
                    Plugin.Components.create('DensityControls_2fo-fc', function (ctx) { return new Bootstrap.Components.Transform.Updater(ctx, 'density-2fo-fc', 'Density: 2Fo-Fc'); }, Plugin.Views.Transform.Updater)(LayoutRegion.Right),
                    Plugin.Components.create('DensityControls_+fo-fc', function (ctx) { return new Bootstrap.Components.Transform.Updater(ctx, 'density-fo-fc1', 'Density: Fo-Fc (+3σ)'); }, Plugin.Views.Transform.Updater)(LayoutRegion.Right),
                    Plugin.Components.create('DensityControls_-fo-fc', function (ctx) { return new Bootstrap.Components.Transform.Updater(ctx, 'density-fo-fc2', 'Density: Fo-Fc (-3σ)'); }, Plugin.Views.Transform.Updater)(LayoutRegion.Right),
                    Plugin.Components.Context.Log(LayoutRegion.Bottom, true),
                    Plugin.Components.Context.Overlay(LayoutRegion.Root),
                    Plugin.Components.Context.Toast(LayoutRegion.Main, true),
                    Plugin.Components.Context.BackgroundTasks(LayoutRegion.Main, true)
                ],
                viewport: {
                    view: Views.Visualization.Viewport,
                    controlsView: Views.Visualization.ViewportControls
                },
                layoutView: Views.Layout,
                tree: void 0 //{ region: LayoutRegion.Left, view: Views.Entity.Tree }
            };
            var plugin = Plugin.create({ target: target, customSpecification: customSpecification, layoutState: { isExpanded: false } });
            plugin.context.logger.message("LiteMol Plugin " + Plugin.VERSION.number);
            return plugin;
        }
        AngularExample.createPlugin = createPlugin;
        function load(plugin, modelUrl, fofcUrl, twofofcUrl) {
            var action = plugin.createTransform();
            var model = action.add(plugin.root, Transformer.Data.Download, {
                url: modelUrl,
                type: 'String'
            }).then(Transformer.Molecule.CreateFromData, {
                format: LiteMol.Core.Formats.Molecule.SupportedFormats.PDB
            }).then(Transformer.Molecule.CreateModel, {
                modelIndex: 0
            }).then(Transformer.Molecule.CreateSelectionFromQuery, {
                query: LiteMol.Core.Structure.Query.hetGroups(),
                name: 'Ligand',
                silent: true
            }, { isBinding: true }).then(Transformer.Molecule.CreateMacromoleculeVisual, {
                het: true, polymer: true, water: true
            });
            // load densities
            action.add(plugin.root, Transformer.Data.Download, {
                url: twofofcUrl,
                type: 'Binary',
                description: '2Fo-Fc Density'
            })
                .then(Transformer.Density.ParseData, {
                format: LiteMol.Core.Formats.Density.SupportedFormats.CCP4,
                id: '2Fo-Fc Density'
            }, { isBinding: true })
                .then(Transformer.Density.CreateVisualBehaviour, {
                id: '2Fo-Fc Density',
                isoSigmaMin: 0,
                isoSigmaMax: 2,
                radius: 5,
                minRadius: 0,
                maxRadius: 10,
                showFull: false,
                style: Bootstrap.Visualization.Density.Style.create({
                    isoValue: 0.8,
                    isoValueType: Bootstrap.Visualization.Density.IsoValueType.Sigma,
                    color: LiteMol.Visualization.Color.fromHex(0x3362B2),
                    isWireframe: true,
                    transparency: {
                        alpha: 0.9,
                    }
                })
            }, { ref: 'density-2fo-fc' });
            var fofc = action.add(plugin.root, Transformer.Data.Download, {
                url: fofcUrl,
                type: 'Binary',
                description: 'Fo-Fc +3σ Density'
            }).then(Transformer.Density.ParseData, { format: LiteMol.Core.Formats.Density.SupportedFormats.CCP4, id: 'Fo-Fc +3σ Density' }, { isBinding: true });
            fofc.then(Transformer.Density.CreateVisualBehaviour, {
                id: 'Fo-Fc +3σ Density',
                isoSigmaMin: 0,
                isoSigmaMax: 5,
                radius: 5,
                showFull: false,
                minRadius: 0,
                maxRadius: 10,
                style: Bootstrap.Visualization.Density.Style.create({
                    isoValue: 3,
                    isoValueType: Bootstrap.Visualization.Density.IsoValueType.Sigma,
                    color: LiteMol.Visualization.Color.fromHex(0x33BB33),
                    isWireframe: true,
                    transparency: {
                        alpha: 0.9,
                    }
                })
            }, { ref: 'density-fo-fc1' });
            fofc.then(Transformer.Density.CreateVisualBehaviour, {
                id: 'Fo-Fc -3σ Density',
                isoSigmaMin: -5,
                isoSigmaMax: 0,
                radius: 5,
                showFull: false,
                minRadius: 0,
                maxRadius: 10,
                style: Bootstrap.Visualization.Density.Style.create({
                    isoValue: -3,
                    isoValueType: Bootstrap.Visualization.Density.IsoValueType.Sigma,
                    color: LiteMol.Visualization.Color.fromHex(0xBB3333),
                    isWireframe: true,
                    transparency: {
                        alpha: 0.9,
                    }
                })
            }, { ref: 'density-fo-fc2' });
            plugin.applyTransform(action);
        }
        AngularExample.load = load;
    })(AngularExample = LiteMol.AngularExample || (LiteMol.AngularExample = {}));
})(LiteMol || (LiteMol = {}));
angular.module('ligands.density', [])
    .controller('LiteMolDensityController', ['$scope', function ($scope) {
        var plugin = LiteMol.AngularExample.createPlugin(document.getElementById('LiteMol'));
        $scope.destroy = function () {
            if (!plugin)
                return;
            plugin.destroy();
            plugin = void 0;
        };
        $scope.load = function (modelUrl, fofcUrl, twofofcUrl) { return LiteMol.AngularExample.load(plugin, modelUrl, fofcUrl, twofofcUrl); };
    }])
    .directive('densityDisplay', function () {
    return {
        template: '<div id="LiteMol" style="width:600px;height:400px;z-index:100000;position:relative; margin-top: 80px; margin-left: 80px">Hi</div>',
        controller: 'LiteMolDensityController',
        controllerAs: 'controller',
        link: function ($scope, element, attrs) {
            $scope.load(attrs.model, attrs.fofc, attrs.twofofc);
            element.on('$destroy', function () {
                $scope.destroy();
            });
        },
        scope: {
            model: '@',
            fofc: '@',
            twofofc: '@'
        }
    };
});
