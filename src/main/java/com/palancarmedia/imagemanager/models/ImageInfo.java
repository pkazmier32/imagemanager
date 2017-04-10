package com.palancarmedia.imagemanager.models;

public class ImageInfo {
	
	private long imageId;
	private String modelName;
	private String seriesName;
	private String imagePath;
	private int imageRating;
	private int modelId;
	private String bucketName;
	private String imageKey;
	private String tagName;
	
	private static String S3URI = "http://s3.amazonaws.com/";
	
	public ImageInfo(long imageId, String modelName, String seriesName, String imagePath, int imageRating, int modelId, String tagName) {
		
		this.imageId = imageId;
		this.modelName = modelName;
		this.seriesName = seriesName;
		this.imagePath = imagePath;
		this.imageRating = imageRating;
		this.modelId = modelId;
		this.tagName = tagName;
		
		bucketName = this.imagePath.substring(this.imagePath.indexOf("/", S3URI.length()), this.imagePath.indexOf("/", S3URI.length())+1);
		imageKey = this.imagePath.substring(S3URI.length() + this.bucketName.length() + 2);
		
	}
	
	public long getImageId() {
		return imageId;
	}
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getSeriesName() {
		return seriesName;
	}
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public int getImageRating() {
		return imageRating;
	}
	public void setImageRating(int imageRating) {
		this.imageRating = imageRating;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public String getBucketName() {
		
		bucketName = this.imagePath.substring(S3URI.length(), this.imagePath.indexOf("/", S3URI.length()));
		
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getImageKey() {
		
		if (this.bucketName != null)
			imageKey = this.imagePath.substring(S3URI.length() + this.bucketName.length() + 1);
		
		return imageKey;
	}
	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}
	
	public String getTagName() {
		return this.tagName;
	}
	
	public void setTagName(String tag) {
		this.tagName = tag;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder("ModelId: ");
		
		sb.append(this.modelId).append(" ModelName: ").append(this.modelName);
		sb.append(" ImagePath: ").append(this.imagePath);
		sb.append(" ImageRating: ").append(this.imageRating);
		sb.append(" BucketName: ").append(getBucketName());
		sb.append(" ImageKey: ").append(getImageKey());
		sb.append(" TagName: ").append(getTagName());
		
		return sb.toString();
	}
	

}
