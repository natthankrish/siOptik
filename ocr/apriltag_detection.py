from pupil_apriltags import Detector
import numpy as np
import cv2


# Initialize AprilTag detector
at_detector = Detector(
    families="tag16h5",
    nthreads=1,
    quad_decimate=1.0,
    quad_sigma=0.0,
    refine_edges=1,
    decode_sharpening=0.25,
    debug=0,
)

# Load the image
image_path = "test_apriltag.png"
img = cv2.imread(image_path)  # reads an image in the BGR format

# Convert the image to grayscale
gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# Detect AprilTags in the grayscale image
tags = at_detector.detect(
    gray_img, estimate_tag_pose=False, camera_params=None, tag_size=None
)


# # Convert the corners to integer values
corners = tags[0].corners.astype(np.int32)

# # Draw the rectangle
cv2.rectangle(img, tuple(corners[0]), tuple(corners[2]), (0, 0, 255), 2)

# Save the image with the rectangle
output_path = "output_image.jpg"
cv2.imwrite(output_path, img)

print(f"Image saved as {output_path}")