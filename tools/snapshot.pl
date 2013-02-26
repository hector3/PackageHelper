#!/usr/bin/perl

use strict;

my $snapshot_tmp = 'snapshot.tmp';
my $snapshot_out = 'snapshot.png';
my $pix_fmt = 'bgr32';

# capture snapshot to tmp file
`adb pull /dev/graphics/fb0 $snapshot_tmp`;

# get screen resolution
my $screen_width;
my $screen_height;
my $dumpsys_result = `adb shell "dumpsys window"`;
if ($dumpsys_result =~ /SurfaceWidth:\s+(\d+)/) {
	$screen_width = $1;
}
if ($dumpsys_result =~ /SurfaceHeight:\s+(\d+)/) {
	$screen_height = $1;
}
my $screen_resolution = $screen_width . 'x' . $screen_height;

`ffmpeg -vframes 1 -vcodec rawvideo -f rawvideo -pix_fmt $pix_fmt -s $screen_resolution -i $snapshot_tmp -f image2 -vcodec png $snapshot_out`;

unlink $snapshot_tmp;

print 'OK';
