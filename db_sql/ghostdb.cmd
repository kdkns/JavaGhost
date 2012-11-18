@echo off
:: ===========================================================================
:: $Id: ghostdb.cmd,v 1.1 2012/10/02 14:11:39 mackermann Exp $
::
:: Copyright(c) 2001-2012 ERCOT. All rights reserved.
::
:: THIS PROGRAM IS AN UNPUBLISHED  WORK AND TRADE SECRET OF THE COPYRIGHT HOLDER,
:: AND DISTRIBUTED ONLY UNDER RESTRICTION.
::
:: No  part  of  this  program  may be used,  installed,  displayed,  reproduced,
:: distributed or modified  without the express written consent  of the copyright
:: holder.
::
:: EXCEPT AS EXPLICITLY STATED  IN A WRITTEN  AGREEMENT BETWEEN  THE PARTIES, THE
:: SOFTWARE IS PROVIDED AS-IS, WITHOUT WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED,
:: INCLUDING THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A PARTICULAR
:: PURPOSE, NONINFRINGEMENT, PERFORMANCE, AND QUALITY.
:: ============================================================================ -->
cls
SETLOCAL

:: SET RAPTOR_DEV=@!DEV_MODE
:: SET RAPTOR_VERSION=@!VERSION
:: SET RAPTOR_SUBVERSION=@!SUBVERSION
:: SET RAPTOR_RELEASE=@!RELEASE
:: SET RAPTOR_BUILD=@!BUILD

:: if "%RAPTOR_DEV%"=="0" goto version_prod

:version_dev
    echo Raptor Settlement System (*** Internal Development Build ***)
    echo Database Installer 0.0.1 (build 20120813140411)
goto copyright

:version_prod
    echo Raptor Settlement System v%RAPTOR_VERSION%.%RAPTOR_SUBVERSION%.%RAPTOR_RELEASE%.%RAPTOR_BUILD%
goto copyright

:copyright
    echo Copyright(c) ERCOT 2001-2012  All Rights Reserved.
    echo.
goto params

:params    
    if /I "%1"=="INSTALL"     goto install
    if /I "%1"=="UNINSTALL"   goto uninstall
    if /I "%1"=="PATCH"       goto patch
goto noparams

:install
    call ant -q -buildfile install.xml init -Dinstall.mode=install
goto end

:uninstall
    call ant -q -buildfile install.xml init -Dinstall.mode=uninstall
goto end

:patch
    if "%2"=="" goto patch_all
    call ant -q -buildfile install.xml init -Dinstall.mode=patch -Dpatch.file=RAPTOR_Core_v_%RAPTOR_VERSION%_%RAPTOR_SUBVERSION%_%RAPTOR_RELEASE%_%RAPTOR_BUILD%_PATCH_%2.xml
goto end

:noparams
    echo Options:
    echo     raptor install              - Installs all Raptor database components
    echo     raptor uninstall            - Removes all Raptor database components
    echo     raptor patch ^<patch_number^> - Installs Raptor patch file specified by the patch number suffix (i.e. 001, 002, etc.)
    
goto end

:end
ENDLOCAL