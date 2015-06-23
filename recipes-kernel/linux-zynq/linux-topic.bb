DESCRIPTION = "Xilinx Zynq kernel with ADI and Topic extensions"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

KBRANCH = "topic-miami"
SRCREV = "257d501b4dce0e3fea5db5e713c56beaefd30763"
LINUX_VERSION = "3.19"

inherit kernel
require recipes-kernel/linux/linux-dtb.inc

S = "${WORKDIR}/git"

# Using LZO compression in the kernel requires "lzop"
DEPENDS += "lzop-native"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-topic:"

# If you have a local repository, you can set this variable to point to
# another kernel repo. Or to another kernel entirely.
KERNEL_GIT_REPO ?= "git://github.com/topic-embedded-products/linux"

SRC_URI = "\
	${KERNEL_GIT_REPO};branch=${KBRANCH} \
	file://defconfig \
	"

KERNEL_IMAGETYPE = "uImage"
KERNEL_DEVICETREE = "arch/arm/boot/dts/${MACHINE}.dts"
KERNEL_DEVICETREE_topic-miami = "\
	arch/arm/boot/dts/topic-miami-dyplo.dts \
	arch/arm/boot/dts/topic-miami-florida-gen.dts \
	arch/arm/boot/dts/topic-miami-florida-gen-pt.dts \
	arch/arm/boot/dts/topic-miami-florida-med.dts \
	arch/arm/boot/dts/topic-miami-florida-med-pt.dts \
	arch/arm/boot/dts/topic-miami-florida-mio.dts \
	arch/arm/boot/dts/topic-miami-florida-pci.dts \
	arch/arm/boot/dts/topic-miami-florida-pci-pt.dts \
	arch/arm/boot/dts/topic-miami-vice.dts \
	"
# See http://permalink.gmane.org/gmane.linux.kernel.commits.head/371588
KERNEL_EXTRA_ARGS += "LOADADDR=0x00008000"
KERNEL_IMAGEDEST = "tmp/boot"

FILES_kernel-image = "${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}*"

LINUX_VERSION_EXTENSION ?= "-topic"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE = "topic-miami"

KERNEL_FLASH_DEVICE = "/dev/mtd4"

pkg_postinst_kernel-image () {
	if [ "x$D" == "x" ]; then
		if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION} ] ; then
			if grep -q "ubi0:qspi-rootfs" /proc/mounts
			then
				flashcp -v /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION} ${KERNEL_FLASH_DEVICE}
			else
				if [ -f /media/mmcblk0p1/${KERNEL_IMAGETYPE} ]; then
					cp /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION} /media/mmcblk0p1/${KERNEL_IMAGETYPE}
				fi
			fi
			rm -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
		fi
	fi
	true
}
