/*
 *
 *                       ADOBE CONFIDENTIAL
 *                     _ _ _ _ _ _ _ _ _ _ _ _
 *
 * Copyright 2004, Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Adobe Systems Incorporated and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Adobe Systems
 * Incorporated and its suppliers and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright
 * law.  Dissemination of this information or reproduction of this material is
 * strictly forbidden unless prior written permission is obtained from Adobe
 * Systems Incorporated.
 *
 * Author: Peter Sorotokin, 24-JUN-2004
 */

// Declarations necessary to use Tahoe global element/attribute tables (through xda::SplicerDOM)

#ifndef _XDA_TABLE_H
#define _XDA_TABLE_H

#include "xda_core.h"
#include "xda_nodetype.h"

namespace xda
{

struct CustomElementEntry;
class Processor;

enum AttributeFlagsEx
{
	GENERIC_ATTRIBUTE_FLAG =				mdom::GENERIC_ATTRIBUTE_FLAG,
	LINK_ATTRIBUTE_FLAG =					mdom::LINK_ATTRIBUTE_FLAG,
	SELECTOR_LINK_ATTRIBUTE_FLAG =			mdom::SELECTOR_LINK_ATTRIBUTE_FLAG,
	PRESENTATION_ATTRIBUTE_FLAG =			0x00000100, // CSS property/presentation attribute
	PASS_THROUGH_ATTRIBUTE_FLAG =			0x00000200, // attribute is passed through by Splicer (except that links are wrapped)
	IMAGE_SOURCE_ATTRIBUTE_FLAG =			0x00000400, // attribute represents an image source, resolved to Image object
	ATTRIBUTE_SELECTOR_ATTRIBUTE_FLAG =		0x00000800, // selector link attribute that affects not only presentational attributes
	DATA_BINDING_NODE_ATTRIBUTE_FLAG =		0x00001000, // attribute used for data binding to node or value
	DATA_BINDING_NODESET_ATTRIBUTE_FLAG =	0x00002000, // attribute used for data binding to nodeset
	INHERITED_FLAG =						0x00004000, // attribute is inherited by default
	PASS_TO_RENDERER_FLAG =					0x00008000,	// transformer should pass this attribute to the renderer
	SHORTCUT_PROPERTY_FLAG =				0x00010000,	// this is a shortcut property
	SHORTCUT_PROPERTY_MEMBER_FLAG =			0x00020000	// this property is also covered by some shortcut
};

enum ElementFlags
{
	EF_PRESENTATION_ATTRIBUTES = 1,
	EF_CSS_IDENTITY = 2,

	// XBL flags
	EF_XBL_EXPANDED_DOM = 0x100,

	// element handler flags
	EF_HANDLER_SHARE = 0x01000, // share ElementHandler for all element instances of that type (instead of having a private one)
	EF_HANDLER_CACHE = 0x02000, // cache element handler instance instead of attaching it (for non-shared handlers)
	EF_HANDLER_CHANGES = 0x04000, // dispatch low-level attribute change events
	EF_HANDLER_DOM_EVENTS = 0x08000, // dispatch DOM events to this handler
	EF_HANDLER_EXPANDED_DOM = 0x10000 // handler attached to the Node in expanded DOM (after XForm/CSS/SMIL expansion)
};

// Splicer contexts
enum ContextID
{
	CONTEXT_ROOT = 1,
	CONTEXT_LINKED_RESOURCE = 2,
	CONTEXT_HTML_TOP_LEVEL = 3,
	CONTEXT_OO_TOP_LEVEL = 4,
	CONTEXT_RENDER = 5,
	CONTEXT_RENDER_CLIP = 6,
	CONTEXT_FOREIGN_OBJECT = 7,
	CONTEXT_FLOW_ROOT = 8,
	CONTEXT_FLOW = 9,
	CONTEXT_FLOW_TABLE_CAPTION_BEFORE = 10,
	CONTEXT_FLOW_TABLE_BLOCK = 11,
	CONTEXT_FLOW_FORM = 12,
	CONTEXT_FLOW_RENDER = 13,
	CONTEXT_RENDER_FORM = 14,
	CONTEXT_GRADIENT = 15,
	CONTEXT_MAX = 16
};

enum ChangeFlags
{
	CF_TSTATE = 16,
	CF_TRANSFORM = 32,
	CF_SELECTION = 64,
	CF_STROKE_SHAPE = 128,
	CF_FILL_SHAPE = 256,
	CF_TEXT_SHAPE = 512,
	CF_STROKE_PAINT = 1024,
	CF_FILL_PAINT = 2048,
	CF_BLEND = 4096,
	CF_OBJECT_CLIP = 8192,
	CF_OBJECT_MASK = 16384,
	CF_OBJECT_FILTER = 32768,
	CF_ALL = 0xFFFFFFFF
};

extern uft::sref<mdom::AttrConfig> attr_name;
extern uft::sref<mdom::AttrConfig> attr_value;
extern uft::sref<mdom::AttrConfig> attr_content;
extern uft::sref<mdom::AttrConfig> attr_property;
extern uft::sref<mdom::AttrConfig> attr_type;
extern uft::sref<mdom::AttrConfig> attr_src;
extern uft::sref<mdom::AttrConfig> attr_style;
extern uft::sref<mdom::AttrConfig> attr_xsi_type;
extern uft::sref<mdom::AttrConfig> attr_scheme;
extern uft::sref<mdom::AttrConfig> attr_font;
extern uft::sref<mdom::AttrConfig> attr_text_decoration;
extern uft::sref<mdom::AttrConfig> attr__epub_text_emphasis_style;
extern uft::sref<mdom::AttrConfig> attr__adobe_text_emphasis_position;
extern uft::sref<mdom::AttrConfig> attr_font_family;
extern uft::sref<mdom::AttrConfig> attr_background_position;
extern uft::sref<mdom::AttrConfig> attr_background;
extern uft::sref<mdom::AttrConfig> attr_padding;
extern uft::sref<mdom::AttrConfig> attr_margin;
extern uft::sref<mdom::AttrConfig> attr_border_color;
extern uft::sref<mdom::AttrConfig> attr_border_style;
extern uft::sref<mdom::AttrConfig> attr_border_width;
extern uft::sref<mdom::AttrConfig> attr_border_bottom;
extern uft::sref<mdom::AttrConfig> attr_border_left;
extern uft::sref<mdom::AttrConfig> attr_border_right;
extern uft::sref<mdom::AttrConfig> attr_border_top;
extern uft::sref<mdom::AttrConfig> attr_border;
extern uft::sref<mdom::AttrConfig> attr_outline;
extern uft::sref<mdom::AttrConfig> attr_list_style;
extern uft::sref<mdom::AttrConfig> attr_border_spacing;
extern uft::sref<mdom::AttrConfig> attr_fill_opacity;
extern uft::sref<mdom::AttrConfig> attr_fill;
extern uft::sref<mdom::AttrConfig> attr_stroke_opacity;
extern uft::sref<mdom::AttrConfig> attr_stroke;
extern uft::sref<mdom::AttrConfig> attr_stroke_width;
extern uft::sref<mdom::AttrConfig> attr_stroke_linecap;
extern uft::sref<mdom::AttrConfig> attr_stroke_linejoin;
extern uft::sref<mdom::AttrConfig> attr_stroke_miterlimit;
extern uft::sref<mdom::AttrConfig> attr_stroke_dashoffset;
extern uft::sref<mdom::AttrConfig> attr_stroke_dasharray;
extern uft::sref<mdom::AttrConfig> attr_opacity;
extern uft::sref<mdom::AttrConfig> attr_comp_op;
extern uft::sref<mdom::AttrConfig> attr_adobe_knockout;
extern uft::sref<mdom::AttrConfig> attr_enable_background;
extern uft::sref<mdom::AttrConfig> attr_viewport_fill;
extern uft::sref<mdom::AttrConfig> attr_color;
extern uft::sref<mdom::AttrConfig> attr_clip_path;
extern uft::sref<mdom::AttrConfig> attr_clip_rule;
extern uft::sref<mdom::AttrConfig> attr_fill_rule;
extern uft::sref<mdom::AttrConfig> attr_mask;
extern uft::sref<mdom::AttrConfig> attr_cursor;
extern uft::sref<mdom::AttrConfig> attr_oeb_column_number;
extern uft::sref<mdom::AttrConfig> attr_link_mark;
extern uft::sref<mdom::AttrConfig> attr_finished;
extern uft::sref<mdom::AttrConfig> attr_column_dimensions;
extern uft::sref<mdom::AttrConfig> attr_stop_opacity;
extern uft::sref<mdom::AttrConfig> attr_stop_color;
extern uft::sref<mdom::AttrConfig> attr_font_size;
extern uft::sref<mdom::AttrConfig> attr_font_variant;
extern uft::sref<mdom::AttrConfig> attr_font_weight;
extern uft::sref<mdom::AttrConfig> attr_font_style;
extern uft::sref<mdom::AttrConfig> attr_font_stretch;
extern uft::sref<mdom::AttrConfig> attr_margin_top;
extern uft::sref<mdom::AttrConfig> attr_margin_bottom;
extern uft::sref<mdom::AttrConfig> attr_margin_left;
extern uft::sref<mdom::AttrConfig> attr_margin_right;
extern uft::sref<mdom::AttrConfig> attr_x_list_margin;
extern uft::sref<mdom::AttrConfig> attr_space_before;
extern uft::sref<mdom::AttrConfig> attr_space_after;
extern uft::sref<mdom::AttrConfig> attr_start_indent;
extern uft::sref<mdom::AttrConfig> attr_end_indent;
extern uft::sref<mdom::AttrConfig> attr_space_start;
extern uft::sref<mdom::AttrConfig> attr_space_end;
extern uft::sref<mdom::AttrConfig> attr_background_attachment;
extern uft::sref<mdom::AttrConfig> attr_background_image;
extern uft::sref<mdom::AttrConfig> attr_background_image_width;
extern uft::sref<mdom::AttrConfig> attr_background_image_height;
extern uft::sref<mdom::AttrConfig> attr_background_color;
extern uft::sref<mdom::AttrConfig> attr_background_repeat;
extern uft::sref<mdom::AttrConfig> attr_background_position_horizontal;
extern uft::sref<mdom::AttrConfig> attr_background_position_vertical;
extern uft::sref<mdom::AttrConfig> attr_padding_after;
extern uft::sref<mdom::AttrConfig> attr_padding_before;
extern uft::sref<mdom::AttrConfig> attr_padding_start;
extern uft::sref<mdom::AttrConfig> attr_padding_end;
extern uft::sref<mdom::AttrConfig> attr_padding_left;
extern uft::sref<mdom::AttrConfig> attr_padding_right;
extern uft::sref<mdom::AttrConfig> attr_padding_top;
extern uft::sref<mdom::AttrConfig> attr_padding_bottom;
extern uft::sref<mdom::AttrConfig> attr_border_after_color;
extern uft::sref<mdom::AttrConfig> attr_border_after_style;
extern uft::sref<mdom::AttrConfig> attr_border_after_width;
extern uft::sref<mdom::AttrConfig> attr_border_before_color;
extern uft::sref<mdom::AttrConfig> attr_border_before_style;
extern uft::sref<mdom::AttrConfig> attr_border_before_width;
extern uft::sref<mdom::AttrConfig> attr_border_start_color;
extern uft::sref<mdom::AttrConfig> attr_border_start_style;
extern uft::sref<mdom::AttrConfig> attr_border_start_width;
extern uft::sref<mdom::AttrConfig> attr_border_end_color;
extern uft::sref<mdom::AttrConfig> attr_border_end_style;
extern uft::sref<mdom::AttrConfig> attr_border_end_width;
extern uft::sref<mdom::AttrConfig> attr_border_left_color;
extern uft::sref<mdom::AttrConfig> attr_border_left_style;
extern uft::sref<mdom::AttrConfig> attr_border_left_width;
extern uft::sref<mdom::AttrConfig> attr_border_right_color;
extern uft::sref<mdom::AttrConfig> attr_border_right_style;
extern uft::sref<mdom::AttrConfig> attr_border_right_width;
extern uft::sref<mdom::AttrConfig> attr_border_top_color;
extern uft::sref<mdom::AttrConfig> attr_border_top_style;
extern uft::sref<mdom::AttrConfig> attr_border_top_width;
extern uft::sref<mdom::AttrConfig> attr_border_bottom_color;
extern uft::sref<mdom::AttrConfig> attr_border_bottom_style;
extern uft::sref<mdom::AttrConfig> attr_border_bottom_width;
extern uft::sref<mdom::AttrConfig> attr_position;
extern uft::sref<mdom::AttrConfig> attr_top;
extern uft::sref<mdom::AttrConfig> attr_bottom;
extern uft::sref<mdom::AttrConfig> attr_left;
extern uft::sref<mdom::AttrConfig> attr_right;
extern uft::sref<mdom::AttrConfig> attr_block_progression_dimension;
extern uft::sref<mdom::AttrConfig> attr_inline_progression_dimension;
extern uft::sref<mdom::AttrConfig> attr_css_width;
extern uft::sref<mdom::AttrConfig> attr_css_height;
extern uft::sref<mdom::AttrConfig> attr_min_width;
extern uft::sref<mdom::AttrConfig> attr_max_width;
extern uft::sref<mdom::AttrConfig> attr_min_height;
extern uft::sref<mdom::AttrConfig> attr_max_height;
extern uft::sref<mdom::AttrConfig> attr_text_transform;
extern uft::sref<mdom::AttrConfig> attr_letter_spacing;
extern uft::sref<mdom::AttrConfig> attr_word_spacing;
extern uft::sref<mdom::AttrConfig> attr_adobe_hyphenate;
extern uft::sref<mdom::AttrConfig> attr_line_height;
extern uft::sref<mdom::AttrConfig> attr_line_increment;
extern uft::sref<mdom::AttrConfig> attr_text_align;
extern uft::sref<mdom::AttrConfig> attr_text_align_last;
extern uft::sref<mdom::AttrConfig> attr_text_indent;
extern uft::sref<mdom::AttrConfig> attr_last_line_end_indent;
extern uft::sref<mdom::AttrConfig> attr_white_space;
extern uft::sref<mdom::AttrConfig> attr_adobe_min_flow_width;
extern uft::sref<mdom::AttrConfig> attr_adobe_line_snap_position;
extern uft::sref<mdom::AttrConfig> attr_adobe_text_layout;
extern uft::sref<mdom::AttrConfig> attr__epub_line_break;
extern uft::sref<mdom::AttrConfig> attr__epub_writing_mode;
extern uft::sref<mdom::AttrConfig> attr__epub_ruby_position;
extern uft::sref<mdom::AttrConfig> attr__adobe_ruby_align;
extern uft::sref<mdom::AttrConfig> attr__adobe_ruby_overhang;
extern uft::sref<mdom::AttrConfig> attr__epub_text_combine;
extern uft::sref<mdom::AttrConfig> attr_x_list_counter_reset;
extern uft::sref<mdom::AttrConfig> attr_counter_reset;
extern uft::sref<mdom::AttrConfig> attr_counter_increment;
extern uft::sref<mdom::AttrConfig> attr_page_break_after;
extern uft::sref<mdom::AttrConfig> attr_page_break_before;
extern uft::sref<mdom::AttrConfig> attr_page_break_inside;
extern uft::sref<mdom::AttrConfig> attr_outline_color;
extern uft::sref<mdom::AttrConfig> attr_outline_style;
extern uft::sref<mdom::AttrConfig> attr_outline_width;
extern uft::sref<mdom::AttrConfig> attr_display;
extern uft::sref<mdom::AttrConfig> attr_float;
extern uft::sref<mdom::AttrConfig> attr_clear;
extern uft::sref<mdom::AttrConfig> attr_clip;
extern uft::sref<mdom::AttrConfig> attr_overflow;
extern uft::sref<mdom::AttrConfig> attr_visibility;
extern uft::sref<mdom::AttrConfig> attr_table_layout;
extern uft::sref<mdom::AttrConfig> attr_caption_side;
extern uft::sref<mdom::AttrConfig> attr_pointer_events;
extern uft::sref<mdom::AttrConfig> attr_display_align;
extern uft::sref<mdom::AttrConfig> attr_direction;
extern uft::sref<mdom::AttrConfig> attr_unicode_bidi;
extern uft::sref<mdom::AttrConfig> attr_vertical_align;
extern uft::sref<mdom::AttrConfig> attr_alignment_baseline;
extern uft::sref<mdom::AttrConfig> attr_alignment_adjust;
extern uft::sref<mdom::AttrConfig> attr_baseline_shift;
extern uft::sref<mdom::AttrConfig> attr_dominant_baseline;
extern uft::sref<mdom::AttrConfig> attr_z_index;
extern uft::sref<mdom::AttrConfig> attr_adobe_page_master;
extern uft::sref<mdom::AttrConfig> attr_adobe_region;
extern uft::sref<mdom::AttrConfig> attr_adobe_flow_options;
extern uft::sref<mdom::AttrConfig> attr_adobe_flow_priority;
extern uft::sref<mdom::AttrConfig> attr_adobe_flow_linger;
extern uft::sref<mdom::AttrConfig> attr_glyphs;
extern uft::sref<mdom::AttrConfig> attr_referenceBox;
extern uft::sref<mdom::AttrConfig> attr_objectData;
extern uft::sref<mdom::AttrConfig> attr_page_x;
extern uft::sref<mdom::AttrConfig> attr_page_y;
extern uft::sref<mdom::AttrConfig> attr__adobe_allow_highlight;
extern uft::sref<mdom::AttrConfig> attr_host_width;
extern uft::sref<mdom::AttrConfig> attr_host_height;
extern uft::sref<mdom::AttrConfig> attr_page_layout;
extern uft::sref<mdom::AttrConfig> attr_page_margin;
extern uft::sref<mdom::AttrConfig> attr_page_width;
extern uft::sref<mdom::AttrConfig> attr_page_height;
extern uft::sref<mdom::AttrConfig> attr_master_name;
extern uft::sref<mdom::AttrConfig> attr_master_reference;
extern uft::sref<mdom::AttrConfig> attr_maximum_repeats;
extern uft::sref<mdom::AttrConfig> attr_flow_name;
extern uft::sref<mdom::AttrConfig> attr_internal_writing_mode;
extern uft::sref<mdom::AttrConfig> attr_region_side;
extern uft::sref<mdom::AttrConfig> attr_region_info;
extern uft::sref<mdom::AttrConfig> attr_blank_or_not_blank;
extern uft::sref<mdom::AttrConfig> attr_odd_or_even;
extern uft::sref<mdom::AttrConfig> attr_page_position;
extern uft::sref<mdom::AttrConfig> attr_min_page_width;
extern uft::sref<mdom::AttrConfig> attr_min_page_height;
extern uft::sref<mdom::AttrConfig> attr_condition;
extern uft::sref<mdom::AttrConfig> attr_page;
extern uft::sref<mdom::AttrConfig> attr_required_flows;
extern uft::sref<mdom::AttrConfig> attr_extent;
extern uft::sref<mdom::AttrConfig> attr_precedence;
extern uft::sref<mdom::AttrConfig> attr_region_name;
extern uft::sref<mdom::AttrConfig> attr_column_count;
extern uft::sref<mdom::AttrConfig> attr_column_gap;
extern uft::sref<mdom::AttrConfig> attr_next_region;
extern uft::sref<mdom::AttrConfig> attr_region_overlap;
extern uft::sref<mdom::AttrConfig> attr_initial_page_number;
extern uft::sref<mdom::AttrConfig> attr_force_page_count;
extern uft::sref<mdom::AttrConfig> attr_block_span;
extern uft::sref<mdom::AttrConfig> attr_content_width;
extern uft::sref<mdom::AttrConfig> attr_content_height;
extern uft::sref<mdom::AttrConfig> attr_scaling;
extern uft::sref<mdom::AttrConfig> attr_scaling_method;
extern uft::sref<mdom::AttrConfig> attr_intrinsic_width;
extern uft::sref<mdom::AttrConfig> attr_intrinsic_height;
extern uft::sref<mdom::AttrConfig> attr_external_destination;
extern uft::sref<mdom::AttrConfig> attr_show_destination;
extern uft::sref<mdom::AttrConfig> attr_border_collapse;
extern uft::sref<mdom::AttrConfig> attr_column_width;
extern uft::sref<mdom::AttrConfig> attr_number_columns_repeated;
extern uft::sref<mdom::AttrConfig> attr_column_number;
extern uft::sref<mdom::AttrConfig> attr_empty_cells;
extern uft::sref<mdom::AttrConfig> attr_starts_row;
extern uft::sref<mdom::AttrConfig> attr_ends_row;
extern uft::sref<mdom::AttrConfig> attr_number_columns_spanned;
extern uft::sref<mdom::AttrConfig> attr_number_rows_spanned;
extern uft::sref<mdom::AttrConfig> attr_list_list_style_image;
extern uft::sref<mdom::AttrConfig> attr_list_list_style_position;
extern uft::sref<mdom::AttrConfig> attr_list_list_style_type;
extern uft::sref<mdom::AttrConfig> attr_relative_align;
extern uft::sref<mdom::AttrConfig> attr_maximum_characters;
extern uft::sref<mdom::AttrConfig> attr_ref_id;
extern uft::sref<mdom::AttrConfig> attr_leader_alignment;
extern uft::sref<mdom::AttrConfig> attr_leader_length;
extern uft::sref<mdom::AttrConfig> attr_leader_pattern;
extern uft::sref<mdom::AttrConfig> attr_leader_pattern_width;
extern uft::sref<mdom::AttrConfig> attr_orphans;
extern uft::sref<mdom::AttrConfig> attr_widows;
extern uft::sref<mdom::AttrConfig> attr_uainfo_name;
extern uft::sref<mdom::AttrConfig> attr_colspan;
extern uft::sref<mdom::AttrConfig> attr_rowspan;
extern uft::sref<mdom::AttrConfig> attr_align;
extern uft::sref<mdom::AttrConfig> attr_alt;
extern uft::sref<mdom::AttrConfig> attr_table_cell_align;
extern uft::sref<mdom::AttrConfig> attr_table_cell_valign;
extern uft::sref<mdom::AttrConfig> attr_table_span;
extern uft::sref<mdom::AttrConfig> attr_valign;
extern uft::sref<mdom::AttrConfig> attr_bgcolor;
extern uft::sref<mdom::AttrConfig> attr_size_width;
extern uft::sref<mdom::AttrConfig> attr_size_height;
extern uft::sref<mdom::AttrConfig> attr_cellspacing;
extern uft::sref<mdom::AttrConfig> attr_cellpadding;
extern uft::sref<mdom::AttrConfig> attr_pre_width;
extern uft::sref<mdom::AttrConfig> attr_object_data;
extern uft::sref<mdom::AttrConfig> attr_object_classid;
extern uft::sref<mdom::AttrConfig> attr_object_codebase;
extern uft::sref<mdom::AttrConfig> attr_object_codetype;
extern uft::sref<mdom::AttrConfig> attr_table_border;
extern uft::sref<mdom::AttrConfig> attr_table_frame;
extern uft::sref<mdom::AttrConfig> attr_table_rules;
extern uft::sref<mdom::AttrConfig> attr_xhtml_link_rel;
extern uft::sref<mdom::AttrConfig> attr_xhtml_link_href;
extern uft::sref<mdom::AttrConfig> attr_xhtml_link_type;
extern uft::sref<mdom::AttrConfig> attr_media;
extern uft::sref<mdom::AttrConfig> attr_dim_x;
extern uft::sref<mdom::AttrConfig> attr_dim_y;
extern uft::sref<mdom::AttrConfig> attr_dim_width;
extern uft::sref<mdom::AttrConfig> attr_dim_height;
extern uft::sref<mdom::AttrConfig> attr_tdim_x;
extern uft::sref<mdom::AttrConfig> attr_tdim_y;
extern uft::sref<mdom::AttrConfig> attr_tdim_width;
extern uft::sref<mdom::AttrConfig> attr_tdim_height;
extern uft::sref<mdom::AttrConfig> attr_rx;
extern uft::sref<mdom::AttrConfig> attr_ry;
extern uft::sref<mdom::AttrConfig> attr_r;
extern uft::sref<mdom::AttrConfig> attr_cx;
extern uft::sref<mdom::AttrConfig> attr_cy;
extern uft::sref<mdom::AttrConfig> attr_x1;
extern uft::sref<mdom::AttrConfig> attr_y1;
extern uft::sref<mdom::AttrConfig> attr_x2;
extern uft::sref<mdom::AttrConfig> attr_y2;
extern uft::sref<mdom::AttrConfig> attr_transform;
extern uft::sref<mdom::AttrConfig> attr_d;
extern uft::sref<mdom::AttrConfig> attr_polyline_points;
extern uft::sref<mdom::AttrConfig> attr_polygon_points;
extern uft::sref<mdom::AttrConfig> attr_gradient_cx;
extern uft::sref<mdom::AttrConfig> attr_gradient_cy;
extern uft::sref<mdom::AttrConfig> attr_gradient_r;
extern uft::sref<mdom::AttrConfig> attr_gradient_fx;
extern uft::sref<mdom::AttrConfig> attr_gradient_fy;
extern uft::sref<mdom::AttrConfig> attr_gradient_href;
extern uft::sref<mdom::AttrConfig> attr_gradient_gradientUnits;
extern uft::sref<mdom::AttrConfig> attr_gradient_spreadMethod;
extern uft::sref<mdom::AttrConfig> attr_gradient_gradientTransform;
extern uft::sref<mdom::AttrConfig> attr_pattern_href;
extern uft::sref<mdom::AttrConfig> attr_pattern_patternUnits;
extern uft::sref<mdom::AttrConfig> attr_pattern_patternContentUnits;
extern uft::sref<mdom::AttrConfig> attr_pattern_patternTransform;
extern uft::sref<mdom::AttrConfig> attr_pattern_x;
extern uft::sref<mdom::AttrConfig> attr_pattern_y;
extern uft::sref<mdom::AttrConfig> attr_pattern_width;
extern uft::sref<mdom::AttrConfig> attr_pattern_height;
extern uft::sref<mdom::AttrConfig> attr_clipPath_clipPathUnits;
extern uft::sref<mdom::AttrConfig> attr_mask_maskUnits;
extern uft::sref<mdom::AttrConfig> attr_mask_maskContentUnits;
extern uft::sref<mdom::AttrConfig> attr_mask_x;
extern uft::sref<mdom::AttrConfig> attr_mask_y;
extern uft::sref<mdom::AttrConfig> attr_mask_width;
extern uft::sref<mdom::AttrConfig> attr_mask_height;
extern uft::sref<mdom::AttrConfig> attr_viewBox;
extern uft::sref<mdom::AttrConfig> attr_preserveAspectRatio;
extern uft::sref<mdom::AttrConfig> attr_svgtext_x;
extern uft::sref<mdom::AttrConfig> attr_svgtext_y;
extern uft::sref<mdom::AttrConfig> attr_svgtext_dx;
extern uft::sref<mdom::AttrConfig> attr_svgtext_dy;
extern uft::sref<mdom::AttrConfig> attr_svgtext_rotate;
extern uft::sref<mdom::AttrConfig> attr_svgtext_textLength;
extern uft::sref<mdom::AttrConfig> attr_svgtext_lengthAdjust;
extern uft::sref<mdom::AttrConfig> attr_text_anchor;
extern uft::sref<mdom::AttrConfig> attr_offset;
extern uft::sref<mdom::AttrConfig> attr_fontface_font_family;
extern uft::sref<mdom::AttrConfig> attr_fontface_font_variant;
extern uft::sref<mdom::AttrConfig> attr_fontface_font_weight;
extern uft::sref<mdom::AttrConfig> attr_fontface_font_style;
extern uft::sref<mdom::AttrConfig> attr_fontface_units_per_em;
extern uft::sref<mdom::AttrConfig> attr_fontface_unicode_range;
extern uft::sref<mdom::AttrConfig> attr_string;
extern uft::sref<mdom::AttrConfig> attr_xlink_href;
extern uft::sref<mdom::AttrConfig> attr_script_xlink_href;
extern uft::sref<mdom::AttrConfig> attr_image_xlink_href;
extern uft::sref<mdom::AttrConfig> attr_font_xlink_href;
extern uft::sref<mdom::AttrConfig> attr_hyperlink_xlink_href;
extern uft::sref<mdom::AttrConfig> attr_hyperlink_target;
extern uft::sref<mdom::AttrConfig> attr_hyperlink_href;
extern uft::sref<mdom::AttrConfig> attr_element;
extern uft::sref<mdom::AttrConfig> attr_include;
extern uft::sref<mdom::AttrConfig> attr_dpdf_dataBinding;
extern uft::sref<mdom::AttrConfig> attr_dpdf_attach;
extern uft::sref<mdom::AttrConfig> attr_dpdf_presentationAttributes;
extern uft::sref<mdom::AttrConfig> attr_bindings;
extern uft::sref<mdom::AttrConfig> attr_trait_name;
extern uft::sref<mdom::AttrConfig> attr_trait_type;
extern uft::sref<mdom::AttrConfig> attr_attribute_name;
extern uft::sref<mdom::AttrConfig> attr_rule_selector;
extern uft::sref<mdom::AttrConfig> attr_rule_condition;
extern uft::sref<mdom::AttrConfig> attr_context;
extern uft::sref<mdom::AttrConfig> attr_nodeType;
extern uft::sref<mdom::AttrConfig> attr_nodeName;
extern uft::sref<mdom::AttrConfig> attr_case;
extern uft::sref<mdom::AttrConfig> attr_match;
extern uft::sref<mdom::AttrConfig> attr_sized_content_width;
extern uft::sref<mdom::AttrConfig> attr_sized_content_height;
extern uft::sref<mdom::AttrConfig> attr_sized_content_ref;
extern uft::sref<mdom::AttrConfig> attr_chunks;
extern uft::sref<mdom::AttrConfig> attr_required_namespace;
extern uft::sref<mdom::AttrConfig> attr_required_modules;
extern uft::sref<mdom::AttrConfig> attr_style_name;
extern uft::sref<mdom::AttrConfig> attr_oo_name;
extern uft::sref<mdom::AttrConfig> attr_oo_family;
extern uft::sref<mdom::AttrConfig> attr_oo_color;
extern uft::sref<mdom::AttrConfig> attr_oo_background_color;
extern uft::sref<mdom::AttrConfig> attr_oo_font_name;
extern uft::sref<mdom::AttrConfig> attr_oo_font_size;
extern uft::sref<mdom::AttrConfig> attr_oo_font_style;
extern uft::sref<mdom::AttrConfig> attr_oo_font_weight;
extern uft::sref<mdom::AttrConfig> attr_oo_text_align;
extern uft::sref<mdom::AttrConfig> attr_oo_line_break;
extern uft::sref<mdom::AttrConfig> attr_oo_text_indent;
extern uft::sref<mdom::AttrConfig> attr_oo_margin_left;
extern uft::sref<mdom::AttrConfig> attr_oo_margin_right;
extern uft::sref<mdom::AttrConfig> attr_oo_margin_top;
extern uft::sref<mdom::AttrConfig> attr_oo_margin_bottom;
extern uft::sref<mdom::AttrConfig> attr_oo_text_underline_style;
extern uft::sref<mdom::AttrConfig> attr_enc_Algorithm;
extern uft::sref<mdom::AttrConfig> attr_enc_URI;
extern uft::sref<mdom::AttrConfig> attr_enc_Id;
extern uft::sref<mdom::AttrConfig> attr_enc_Type;
extern uft::sref<mdom::AttrConfig> attr_enc_compression;
extern uft::sref<mdom::AttrConfig> attr_scrambleKey;
extern uft::sref<mdom::AttrConfig> attr_method;
extern uft::sref<mdom::AttrConfig> attr_auth;
extern uft::sref<mdom::AttrConfig> attr_critical;
extern uft::sref<mdom::AttrConfig> attr_until;
extern uft::sref<mdom::AttrConfig> attr_keyInfo;
extern uft::sref<mdom::AttrConfig> attr_data;
extern uft::sref<mdom::AttrConfig> attr_initial;
extern uft::sref<mdom::AttrConfig> attr_max;
extern uft::sref<mdom::AttrConfig> attr_incrementInterval;
extern uft::sref<mdom::AttrConfig> attr_identity;
extern uft::sref<mdom::AttrConfig> attr_seed;
extern uft::sref<mdom::AttrConfig> attr_href;
extern uft::sref<mdom::AttrConfig> attr_id;
extern uft::sref<mdom::AttrConfig> attr_relative;

extern uft::sref<mdom::AttrConfig> tattr_width;
extern uft::sref<mdom::AttrConfig> tattr_height;
extern uft::sref<mdom::AttrConfig> tattr_page_width;
extern uft::sref<mdom::AttrConfig> tattr_page_height;
extern uft::sref<mdom::AttrConfig> tattr_em;
extern uft::sref<mdom::AttrConfig> tattr_ex;
extern uft::sref<mdom::AttrConfig> tattr_page_layout;
extern uft::sref<mdom::AttrConfig> tattr_page_margin;

const xpath::Expression& elementsToProcessInDocumentTree();

void configureDOM( mdom::DOM * dom );

uft::sref<mdom::AttrConfig> getAttrConfigForQName( Processor * processor, uft::uint32 nodeType, const uft::QName& name );
uft::sref<mdom::AttrConfig> getCSSPropertyConfig( const uft::String& name );

uft::QName getElementQName( const uft::uint32 nodeType );

uft::uint32 getContextFlags( uft::uint32 context );

const CustomElementEntry * getCustomElementEntry( uft::uint32 nodeType, Processor * dom );
bool isCustomElementType( uft::uint32 nodeType );

uft::Value getResourceById( const uft::String& id );

uft::Dict getCommonAttributeMap( uft::uint32 elementFlags );

}

#endif // _XDA_TABLE
