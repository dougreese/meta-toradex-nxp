DESCRIPTION = "Linux Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

DEPENDS += "lzop-native "

LINUX_VERSION_colibri-vf = "3.0.15"
LINUX_VERSION_apalis-imx6 ?= "3.0.35"

SRCREV_colibri-vf = "1d876acfd2cd445fe46e5bc4c531da2c6bef0c91"
PR_colibri-vf = "V2.3b1"
SRCREV_apalis-imx6 = "fbff978ea77f9d0832cc924e91b2497d7cde572c"
PR_apalis-imx6 = "V2.2b1"

PV = "${LINUX_VERSION}+gitr${SRCREV}"
S = "${WORKDIR}/git"
SRCBRANCH_colibri-vf = "colibri_vf"
SRCBRANCH_apalis-imx6 = "toradex_imx6"
SRC_URI = "git://git.toradex.com/linux-toradex.git;protocol=git;branch=${SRCBRANCH}"
# a Patch
# SRC_URI += "file://a.patch "

COMPATIBLE_MACHINE = "(colibri-vf|apalis-imx6)"

# Place changes to the defconfig here
config_script () {
#    #example change to the .config
#    #sets CONFIG_TEGRA_CAMERA unconditionally to 'y'
#    sed -i -e /CONFIG_TEGRA_CAMERA/d ${S}/.config
#    echo "CONFIG_TEGRA_CAMERA=y" >> ${S}/.config
    echo "dummy" > /dev/null
}

do_configure_prepend () {
    #use the defconfig provided in the kernel source tree
    #assume its called ${MACHINE}_defconfig, but with '_' instead of '-'
    DEFCONFIG="`echo ${MACHINE} | sed -e 's/\-/\_/g' -e 's/$/_defconfig/'`"

    oe_runmake $DEFCONFIG

    #maybe change some configuration
    config_script 
}

# We need to pass it as param since kernel might support more then one
# machine, with different entry points
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

kernel_do_compile() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE
    oe_runmake ${KERNEL_IMAGETYPE_FOR_MAKE} ${KERNEL_ALT_IMAGETYPE} LD="${KERNEL_LD}" ${KERNEL_EXTRA_ARGS}
    if test "${KERNEL_IMAGETYPE_FOR_MAKE}.gz" = "${KERNEL_IMAGETYPE}"; then
        gzip -9c < "${KERNEL_IMAGETYPE_FOR_MAKE}" > "${KERNEL_OUTPUT}"
    fi
}

do_compile_kernelmodules() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE
    if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
        oe_runmake ${PARALLEL_MAKE} modules LD="${KERNEL_LD}"
    else
        bbnote "no modules to compile"
    fi
}

#since daisy oe refuses to install headers in /usr/include/linux, install
#them  in /usr/local/include/linux. This is also in gcc's default include paths
PACKAGES =+ "kernel-fsl-headers-dev"
FILES_kernel-fsl-headers-dev = "${prefix}/local/include/linux/"
do_install_append_colibri-vf() {
    #install vybrid specific headers with definitions used for userspace interaction
    install -d ${D}/${prefix}/local/include/linux/
    install -m 644 ${S}/include/linux/mvf_sema4.h ${D}/${prefix}/local/include/linux/
}

python sysroot_stage_all_append () {
    if os.path.isdir("${D}/${prefix}/local/include/linux/"):
        oe.path.copyhardlinktree(d.expand("${D}/${prefix}/local/include/linux/"), d.expand("${SYSROOT_DESTDIR}/${prefix}/local/include/linux/"))
}
