HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

PV = "2019.07+git${SRCPV}"
LOCALVERSION = "-${TDX_VER_ITEM}"
inherit fsl-u-boot-localversion

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "e60e2c0d3f44f42642bb4dbf648a3b25142fd8d1"
SRCBRANCH = "2019.07-toradex-next"
SRCREV_use-head-next = "${AUTOREV}"
SRCBRANCH_use-head-next = "2019.07-toradex-next"

SRC_URI = "git://git.toradex.com/u-boot-toradex.git;protocol=https;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"

# defaults
TDX_VER_ITEM ??= "0"