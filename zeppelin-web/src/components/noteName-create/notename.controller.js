/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import './note-name-dialog.css'

angular.module('zeppelinWebApp').controller('NotenameCtrl', NotenameCtrl)

function NotenameCtrl ($scope, $rootScope, noteListDataFactory, $routeParams, websocketMsgSrv) {
  'ngInject'

  let vm = this
  vm.clone = false
  vm.notes = noteListDataFactory
  vm.websocketMsgSrv = websocketMsgSrv
  $scope.note = {}
  $scope.interpreterSettings = {}
  $scope.note.defaultInterpreter = null

  // TODO elfowl remove
  $rootScope.isAdmin = function(zeppelinUser) {
    const adminArray = ['jsprowl', 'rohanr', 'pwagle', 'srikantht']
    return (adminArray.indexOf(zeppelinUser) >= 0)
  }

  $rootScope.standardNoteName = function(noteName) {
    return (noteName.startsWith('#Users/')) ||
      (noteName.startsWith('0 Tutorial/')) ||
      (noteName.startsWith('1 Misc/')) ||
      (noteName.startsWith('2 To Be Deleted/')) ||
      (noteName.charAt(0).match(/[a-z]/i) && noteName.includes('/'))
  }

  $rootScope.showNoteNameDialog = function() {
    BootstrapDialog.alert({
      closable: true,
      title: 'Please create notes in #Users or Project folders',
      message: 'To create a note in #Users folder begin note names with "#Users/' +
                 $rootScope.zeppelinUser + '/NOTE_NAME"' +
               '\n' +
               'Project folder names cannot start with numbers or symbols'
    })
  }

  vm.createNote = function() {
    window.console.log('createNote $scope.note.notename: ' + $scope.note.notename)
    if (!$rootScope.isAdmin($rootScope.zeppelinUser) && !$rootScope.standardNoteName($scope.note.notename)) {
      $rootScope.showNoteNameDialog()
      return
    }
    if (!vm.clone) {
      vm.websocketMsgSrv.createNotebook($scope.note.notename)
    } else {
      const noteId = $routeParams.noteId
      vm.websocketMsgSrv.cloneNotebook(noteId, $scope.note.notename)
    }
  }

  vm.handleNameEnter = function () {
    angular.element('#noteNameModal').modal('toggle')
    vm.createNote()
  }

  vm.preVisible = function(clone, sourceNoteName, path) {
    vm.clone = clone
    vm.sourceNoteName = sourceNoteName
    $scope.note.notename = vm.clone ? vm.cloneNoteName() : vm.newNoteName(path)
    $scope.$apply()
  }

  vm.newNoteName = function () {
    let newCount = 1
    angular.forEach(vm.notes.flatList, function (noteName) {
      noteName = noteName.name
      if (noteName.match(/^Untitled Note [0-9]*$/)) {
        let lastCount = noteName.substr(14) * 1
        if (newCount <= lastCount) {
          newCount = lastCount + 1
        }
      }
    })
    return '#Users/' + $rootScope.zeppelinUser + '/Untitled Note ' + newCount
  }

  vm.cloneNoteName = function () {
    let copyCount = 1
    let newCloneName = ''
    let lastIndex = vm.sourceNoteName.lastIndexOf(' ')
    let endsWithNumber = !!vm.sourceNoteName.match('^.+?\\s\\d$')
    let noteNamePrefix = endsWithNumber ? vm.sourceNoteName.substr(0, lastIndex) : vm.sourceNoteName
    let regexp = new RegExp('^' + noteNamePrefix + ' .+')

    angular.forEach(vm.notes.flatList, function (noteName) {
      noteName = noteName.name
      if (noteName.match(regexp)) {
        let lastCopyCount = noteName.substr(lastIndex).trim()
        newCloneName = noteNamePrefix
        lastCopyCount = parseInt(lastCopyCount)
        if (copyCount <= lastCopyCount) {
          copyCount = lastCopyCount + 1
        }
      }
    })

    if (!newCloneName) {
      newCloneName = vm.sourceNoteName
    }
    return newCloneName + ' ' + copyCount
  }

  vm.getInterpreterSettings = function () {
    vm.websocketMsgSrv.getInterpreterSettings()
  }

  $scope.$on('interpreterSettings', function (event, data) {
    $scope.interpreterSettings = data.interpreterSettings

    // initialize default interpreter with Spark interpreter
    $scope.note.defaultInterpreter = data.interpreterSettings[0]
  })
}
