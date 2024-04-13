from pupil_apriltags import Detector
import numpy as np
import cv2

# Note: only runs on linux

at_detector = Detector(
    families="tag16h5 tag25h9 tag36h11 tagCircle21h7 tagCircle49h12 tagCustom48h12 tagStandard41h12 tagStandard52h13",
    nthreads=1,
    
)

image_path = "test_apriltag.png"
img = cv2.imread(image_path)
gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

tags = at_detector.detect(
    gray_img, estimate_tag_pose=False, camera_params=None, tag_size=None
)

for detection in tags:
    corners = detection.corners.astype(np.int32)
    print("Ini corners : ", corners)
    cv2.rectangle(img, tuple(corners[0]), tuple(corners[2]), (0, 0, 255), 5)

output_path = "output_image.jpg"
cv2.imwrite(output_path, img)
print(tags)
print(f"Image saved as {output_path}")